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

class ExtraWidgetsExtension extends DefaultClassManager {

  private var widgetContextManager: WidgetContextManager = null
  private var writer: Writer = null
  private var primitives: Iterable[(String, Primitive)] = null

  override def runOnce(extensionManager: ExtensionManager): Unit = {

    val xwFolder = {
      val xwJarURL = new JarLoader(getWorkspace(extensionManager))
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
      "SELECT-TAB" -> new SelectTab(writer, getWorkspace(extensionManager)),
      "ON-CHANGE" -> new OnChange(writer, kindInfo, widgetContextManager))

    val kindListPrimitives: Iterable[(String, Primitive)] =
      for {
        (kindName, pluralName) ← widgetKinds.mapValues(_.pluralName)
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
        .mapValues(_.map(_._2).reduce(_ | _)) // and reduce the syntaxType constant
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

    primitives =
      staticPrimitives ++ constructorPrimitives ++
        kindListPrimitives ++ getters ++ setters ++ changeSubscribers

    for (app ← getApp(extensionManager))
      new GUI(app, writer, widgetKinds)
  }

  def load(primitiveManager: PrimitiveManager): Unit =
    for ((name, prim) ← primitives)
      primitiveManager.addPrimitive(name, prim)

  override def unload(em: ExtensionManager): Unit = writer.clearAll()

}
