package uk.ac.surrey.xw.extension

import java.io.File

import org.nlogo.api.DefaultClassManager
import org.nlogo.api.ExtensionException
import org.nlogo.api.ExtensionManager
import org.nlogo.api.PrimitiveManager
import org.nlogo.core.Primitive
import org.nlogo.workspace.JarLoader

import uk.ac.surrey.xw.WidgetsLoader
import uk.ac.surrey.xw.api.KindName
import uk.ac.surrey.xw.api.PropertyKey
import uk.ac.surrey.xw.api.TabKind
import uk.ac.surrey.xw.api.WidgetKind
import uk.ac.surrey.xw.extension.prim.Ask
import uk.ac.surrey.xw.extension.prim.ClearAll
import uk.ac.surrey.xw.extension.prim.Create
import uk.ac.surrey.xw.extension.prim.Export
import uk.ac.surrey.xw.extension.prim.Get
import uk.ac.surrey.xw.extension.prim.GetProperty
import uk.ac.surrey.xw.extension.prim.Import
import uk.ac.surrey.xw.extension.prim.KindList
import uk.ac.surrey.xw.extension.prim.Of
import uk.ac.surrey.xw.extension.prim.OnChange
import uk.ac.surrey.xw.extension.prim.OnChangeProperty
import uk.ac.surrey.xw.extension.prim.Remove
import uk.ac.surrey.xw.extension.prim.SelectTab
import uk.ac.surrey.xw.extension.prim.Set
import uk.ac.surrey.xw.extension.prim.SetProperty
import uk.ac.surrey.xw.extension.prim.Widgets
import uk.ac.surrey.xw.extension.prim.With
import uk.ac.surrey.xw.extension.util.getApp
import uk.ac.surrey.xw.extension.util.getWorkspace
import uk.ac.surrey.xw.gui.GUI
import uk.ac.surrey.xw.state.Writer
import uk.ac.surrey.xw.state.newMutableWidgetMap
import org.nlogo.workspace.AbstractWorkspace

class ExtraWidgetsExtension extends DefaultClassManager {

  private var widgetContextManager: WidgetContextManager = null
  private var writer: Writer = null
  private var primitives: Iterable[(String, Primitive)] = null

  override def runOnce(extensionManager: ExtensionManager): Unit = {
    val workspace = getWorkspace(extensionManager)

    val xwFolder = {
      val xwJarURL = new JarLoader(workspace)
        .locateExtension("xw")
        .getOrElse(throw new ExtensionException("Can't locate xw extension folder."))
      new File(xwJarURL.toURI).getParentFile
    }

    val widgetKinds: Map[KindName, WidgetKind[_]] =
      WidgetsLoader.loadWidgetKinds(xwFolder)

    locally {
      val widgetMap = newMutableWidgetMap
      writer = new Writer(widgetMap, widgetKinds)
      widgetContextManager = new WidgetContextManager
    }

    val kindInfo = new KindInfo(writer, widgetKinds)
    primitives = primitiveList(writer, widgetKinds, widgetContextManager, kindInfo, workspace)

    for (app ← getApp(extensionManager))
      new GUI(app, writer, widgetKinds)
  }

  private def primitiveList(
    writer: Writer,
    widgetKinds: Map[KindName, WidgetKind[_]],
    widgetContextManager: WidgetContextManager,
    kindInfo: KindInfo,
    workspace: AbstractWorkspace): Iterable[(String, Primitive)] = {
    val staticPrimitives: Iterable[(String, Primitive)] = Seq(
      "ASK" -> new Ask(widgetContextManager),
      "OF" -> new Of(widgetContextManager),
      "WITH" -> new With(widgetContextManager),
      "GET" -> new Get(writer, kindInfo, widgetContextManager),
      "SET" -> new Set(writer, kindInfo, widgetContextManager),
      "REMOVE" -> new Remove(writer),
      "WIDGETS" -> new Widgets(writer),
      "CLEAR-ALL" -> new ClearAll(writer),
      "EXPORT" -> new Export(writer),
      "IMPORT" -> new Import(writer),
      "SELECT-TAB" -> new SelectTab(writer, workspace),
      "ON-CHANGE" -> new OnChange(writer, kindInfo, widgetContextManager))

    val kindListPrimitives: Iterable[(String, Primitive)] =
      for {
        (kindName, pluralName) ← widgetKinds.view.mapValues(_.pluralName).toMap
      } yield pluralName -> new KindList(kindName, writer)

    val constructorPrimitives: Iterable[(String, Primitive)] =
      widgetKinds.keys.map { kindName ⇒
        ("CREATE-" + kindName) -> new Create(kindName, writer, widgetContextManager)
      }

    // When building getters and setters for properties that are
    // multiply defined, we fold their syntactic indications together
    // by using bitwise ORs (.reduce(_ | _))
    def reduceProperties(includeReadOnly: Boolean): Map[PropertyKey, Int] = {
      val syntaxTypes = for {
        kind <- widgetKinds.values
        property <- kind.properties.values
        if includeReadOnly || !property.readOnly
      } yield (property.key, property.syntaxType)
      syntaxTypes
        .groupBy(_._1) // group by property key
        .view
        .mapValues(_.map(_._2).reduce(_ | _)) // and reduce the syntaxType constant
        .toMap
    }

    val getters: Iterable[(String, Primitive)] = for {
      (key, outputType) ← reduceProperties(includeReadOnly = true)
      getter = new GetProperty(writer, key, outputType, widgetContextManager)
    } yield key -> getter

    val setters: Iterable[(String, Primitive)] = for {
      (key, inputType) ← reduceProperties(includeReadOnly = false)
      setter = new SetProperty(writer, key, inputType, kindInfo, widgetContextManager)
    } yield ("SET-" + key) -> setter

    val changeSubscribers: Iterable[(String, Primitive)] = for {
      (key, _) ← reduceProperties(includeReadOnly = false)
      onChange = new OnChangeProperty(writer, key, widgetContextManager)
    } yield ("ON-" + key + "-CHANGE") -> onChange

    staticPrimitives ++ constructorPrimitives ++
      kindListPrimitives ++ getters ++ setters ++ changeSubscribers
  }

  private def primitiveMetadataFallback(): Iterable[(String, Primitive)] = {
    val widgetKinds = {
      val kinds = WidgetsLoader.loadWidgetKindsFromJars(
        buildWidgetJars,
        getClass.getClassLoader)
      (new TabKind +: kinds.toSeq)
        .map(kind => kind.name -> kind)
        .toMap
    }
    val metadataWriter = new Writer(newMutableWidgetMap, widgetKinds)
    val metadataKindInfo = new KindInfo(metadataWriter, widgetKinds)
    primitiveList(
      metadataWriter,
      widgetKinds,
      new WidgetContextManager,
      metadataKindInfo,
      null)
  }

  private def buildWidgetJars: Seq[File] = {
    val codeSource = Option(getClass.getProtectionDomain.getCodeSource)
      .getOrElse(throw new ExtensionException("Can't locate xw extension jar."))
    val extensionJar = new File(codeSource.getLocation.toURI)
    val projectCandidates = Seq(
      Option(extensionJar.getParentFile)
        .flatMap(file => Option(file.getParentFile))
        .flatMap(file => Option(file.getParentFile)),
      Some(new File("xw").getAbsoluteFile),
      Some(new File(".").getAbsoluteFile)
    ).flatten.distinct
    val jars = projectCandidates
      .flatMap(projectDir => widgetJarsBelow(new File(projectDir, "widgets")))
      .distinct
      .sortBy(_.getAbsolutePath)
    if (jars.isEmpty)
      throw new ExtensionException(
        "Can't locate built xw widget jars below any of: " +
          projectCandidates.map(new File(_, "widgets")).mkString(", ") + ".")
    jars
  }

  private def widgetJarsBelow(widgetsDir: File): Seq[File] = {
    for {
      widgetDir <- listFiles(widgetsDir).toSeq
      if widgetDir.isDirectory
      targetDir = new File(widgetDir, "target")
      scalaTarget <- listFiles(targetDir).toSeq
      if scalaTarget.isDirectory && scalaTarget.getName.startsWith("scala-")
      jar <- listFiles(scalaTarget)
      if jar.isFile && jar.getName.equalsIgnoreCase(widgetDir.getName + ".jar")
    } yield jar
  }

  private def listFiles(file: File): Array[File] =
    Option(file.listFiles).getOrElse(Array.empty)

  def load(primitiveManager: PrimitiveManager): Unit =
    for ((name, prim) ← Option(primitives).getOrElse(primitiveMetadataFallback()))
      primitiveManager.addPrimitive(name, prim)

  override def unload(em: ExtensionManager): Unit =
    if (writer != null)
      writer.clearAll()

}
