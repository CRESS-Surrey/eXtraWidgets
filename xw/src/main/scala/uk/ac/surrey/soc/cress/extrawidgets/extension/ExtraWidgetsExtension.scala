package uk.ac.surrey.soc.cress.extrawidgets.extension

import org.nlogo.api.DefaultClassManager
import org.nlogo.api.ExtensionManager
import org.nlogo.api.Primitive
import org.nlogo.api.PrimitiveManager
import org.nlogo.app.App
import org.nlogo.app.AppFrame
import org.nlogo.window.GUIWorkspace
import uk.ac.surrey.soc.cress.extrawidgets.WidgetsLoader
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKind
import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.Add
import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.AddWidget
import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.Get
import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.GetProperty
import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.Properties
import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.PropertyKeys
import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.Remove
import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.Set
import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.SetProperty
import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.Version
import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.WidgetKeys
import uk.ac.surrey.soc.cress.extrawidgets.gui.Manager
import uk.ac.surrey.soc.cress.extrawidgets.state.Reader
import uk.ac.surrey.soc.cress.extrawidgets.state.Writer
import uk.ac.surrey.soc.cress.extrawidgets.state.newMutableWidgetMap
import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.ClearAll
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyKey
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertySyntax

class ExtraWidgetsExtension extends DefaultClassManager {

  private var writer: Writer = null
  private var reader: Reader = null
  private var primitives: Seq[(String, Primitive)] = null

  override def runOnce(extensionManager: ExtensionManager): Unit = {

    locally {
      val widgetMap = newMutableWidgetMap
      reader = new Reader(widgetMap)
      writer = new Writer(widgetMap, reader)
    }

    val widgetKinds: Map[String, WidgetKind] =
      WidgetsLoader.loadWidgetKinds

    val staticPrimitives = Seq(
      "VERSION" -> new Version("0.0.0-wip"),
      "__ADD" -> new Add(writer),
      "__SET" -> new Set(writer),
      "__GET" -> new Get(reader),
      "REMOVE" -> new Remove(writer),
      "WIDGET-KEYS" -> new WidgetKeys(reader),
      "PROPERTY-KEYS" -> new PropertyKeys(reader),
      "PROPERTIES" -> new Properties(reader),
      "CLEAR-ALL" -> new ClearAll(writer)
    )

    val widgetPrimitives = widgetKinds.keys.map { kindName ⇒
      ("ADD-" + kindName) -> new AddWidget(writer, kindName)
    }

    // multiple widget kinds may define properties with the same key,
    // so we group each of these keys with all their possible syntaxes,
    // which we will "collapse" together when building primitives
    // by using bitwise ORs (.reduce(_ | _))
    val keysToSyntaxes: Map[PropertyKey, Iterable[PropertySyntax]] =
      widgetKinds.values.flatMap(_.syntaxes).groupBy(_._1).mapValues(_.unzip._2)

    val getters = for {
      (key, syntaxes) ← keysToSyntaxes
      name = "GET-" + key
      outputType = syntaxes.map(_.outputType).reduce(_ | _)
      prim = new GetProperty(reader, key, outputType)
    } yield name -> prim

    val setters = for {
      (key, syntaxes) ← keysToSyntaxes
      name = "SET-" + key
      inputType = syntaxes.map(_.inputType).reduce(_ | _)
      prim = new SetProperty(writer, key, inputType)
    } yield name -> prim

    primitives = staticPrimitives ++ widgetPrimitives ++ getters ++ setters

    Seq(extensionManager)
      .collect { case em: org.nlogo.workspace.ExtensionManager ⇒ em }
      .map(_.workspace)
      .collect { case ws: GUIWorkspace ⇒ ws }
      .map(_.getFrame)
      .collect { case af: AppFrame ⇒ af }
      .flatMap(_.getLinkChildren)
      .collect { case app: App ⇒ app }
      .foreach { app ⇒
        new Manager(app, reader, writer, widgetKinds)
      }

  }

  def load(primitiveManager: PrimitiveManager): Unit = {
    println("load() " + this)

    println("Loaded primitives: " + primitives.unzip._1.toList)
    for ((name, prim) ← primitives)
      primitiveManager.addPrimitive(name, prim)
  }

  override def unload(em: ExtensionManager): Unit = {
    println("unload() " + this)
    clearAll()
  }

  override def clearAll(): Unit = writer.clearAll()
}
