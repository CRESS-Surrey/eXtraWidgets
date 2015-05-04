package uk.ac.surrey.xw.extension

import java.io.File
import java.net.URI

import org.nlogo.api.DefaultClassManager
import org.nlogo.api.ExtensionManager
import org.nlogo.api.Primitive
import org.nlogo.api.PrimitiveManager

import uk.ac.surrey.xw.WidgetsLoader
import uk.ac.surrey.xw.api.KindName
import uk.ac.surrey.xw.api.WidgetKind
import uk.ac.surrey.xw.extension.prim._
import uk.ac.surrey.xw.extension.util.getApp
import uk.ac.surrey.xw.extension.util.getWorkspace
import uk.ac.surrey.xw.gui.GUI
import uk.ac.surrey.xw.state.Writer
import uk.ac.surrey.xw.state.newMutableWidgetMap

class ExtraWidgetsExtension extends DefaultClassManager {

  private var widgetContextManager: WidgetContextManager = null
  private var writer: Writer = null
  private var primitives: Seq[(String, Primitive)] = null

  override def runOnce(extensionManager: ExtensionManager): Unit = {
    // Note that new File(new URI(extensionManager.resolvePathAsURL("xw")) does not work with
    // folders with spaces.
    val xwFolder = new File(extensionManager.resolvePathAsURL("xw").replaceAll("file:", ""))

    val widgetKinds: Map[KindName, WidgetKind[_]] =
      WidgetsLoader.loadWidgetKinds(xwFolder)

    locally {
      val widgetMap = newMutableWidgetMap
      writer = new Writer(widgetMap, widgetKinds)
      widgetContextManager = new WidgetContextManager
    }

    val kindInfo = new KindInfo(writer, widgetKinds)

    val staticPrimitives = Seq(
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
      "ON-CHANGE" -> new OnChange(writer, kindInfo, widgetContextManager)
    )

    val kindListPrimitives = for {
      (kindName, pluralName) ← widgetKinds.mapValues(_.pluralName)
    } yield pluralName -> new KindList(kindName, writer)

    val constructorPrimitives = widgetKinds.keys.map { kindName ⇒
      ("CREATE-" + kindName) -> new Create(kindName, writer, widgetContextManager)
    }

    // When building getters and setters for properties that are
    // multiply defined, we fold their syntactic indications together
    // by using bitwise ORs (.reduce(_ | _))

    val syntaxTypes = for {
      kind ← widgetKinds.values
      property ← kind.properties.values
    } yield (property.key, property.syntaxType, property.readOnly)
    val getters = for {
      (key, outputType) ← syntaxTypes.groupBy(_._1)
        .mapValues(_.unzip3._2.reduce(_ | _))
      getter = new GetProperty(writer, key, outputType, widgetContextManager)
    } yield key -> getter

    val setters = for {
      (key, inputType) ← syntaxTypes.filter(!_._3).groupBy(_._1)
        .mapValues(_.unzip3._2.reduce(_ | _))
      setter = new SetProperty(writer, key, inputType, kindInfo, widgetContextManager)
    } yield ("SET-" + key) -> setter

    val changeSubscribers = for {
      (key, inputType) ← syntaxTypes.filter(!_._3).groupBy(_._1)
        .mapValues(_.unzip3._2.reduce(_ | _))
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
