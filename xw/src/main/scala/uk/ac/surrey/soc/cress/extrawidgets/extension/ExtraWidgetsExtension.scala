package uk.ac.surrey.soc.cress.extrawidgets.extension

import org.nlogo.api.DefaultClassManager
import org.nlogo.api.ExtensionManager
import org.nlogo.api.Primitive
import org.nlogo.api.PrimitiveManager
import org.nlogo.app.App
import org.nlogo.app.AppFrame
import org.nlogo.window.GUIWorkspace

import uk.ac.surrey.soc.cress.extrawidgets.WidgetsLoader
import uk.ac.surrey.soc.cress.extrawidgets.api.KindName
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyKey
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertySyntax
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKind
import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.Ask
import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.ClearAll
import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.Create
import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.Get
import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.GetProperty
import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.Of
import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.Properties
import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.PropertyKeys
import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.Remove
import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.SetProperty
import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.Version
import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.Widgets
import uk.ac.surrey.soc.cress.extrawidgets.gui.Manager
import uk.ac.surrey.soc.cress.extrawidgets.state.Reader
import uk.ac.surrey.soc.cress.extrawidgets.state.Writer
import uk.ac.surrey.soc.cress.extrawidgets.state.newMutableWidgetMap

class ExtraWidgetsExtension extends DefaultClassManager {

  private var widgetContextManager: WidgetContextManager = null
  private var writer: Writer = null
  private var reader: Reader = null
  private var primitives: Seq[(String, Primitive)] = null

  override def runOnce(extensionManager: ExtensionManager): Unit = {

    locally {
      val widgetMap = newMutableWidgetMap
      reader = new Reader(widgetMap)
      writer = new Writer(widgetMap, reader)
      widgetContextManager = new WidgetContextManager
    }

    val widgetKinds: Map[KindName, WidgetKind] =
      WidgetsLoader.loadWidgetKinds

    val defaultProperties: Map[KindName, PropertyKey] =
      widgetKinds.mapValues(_.defaultProperty)
        .collect { case (name, Some(propertyKey)) ⇒ name -> propertyKey }

    val staticPrimitives = Seq(
      "VERSION" -> new Version("0.0.0-wip"),
      "ASK" -> new Ask(widgetContextManager),
      "OF" -> new Of(widgetContextManager),
      "GET" -> new Get(reader, defaultProperties, widgetContextManager),
      "REMOVE" -> new Remove(writer),
      "WIDGETS" -> new Widgets(reader),
      "PROPERTY-KEYS" -> new PropertyKeys(reader),
      "PROPERTIES" -> new Properties(reader),
      "CLEAR-ALL" -> new ClearAll(writer)
    )

    val constructorPrimitives = widgetKinds.keys.map { kindName ⇒
      ("CREATE-" + kindName) -> new Create(kindName, writer, widgetContextManager)
    }

    // multiple widget kinds may define properties with the same key,
    // so we group each of these keys with all their possible syntaxes,
    // which we will "collapse" together when building primitives
    // by using bitwise ORs (.reduce(_ | _))
    val propertySyntaxes: Map[PropertyKey, Iterable[PropertySyntax]] =
      widgetKinds.values.flatMap(_.syntaxes).groupBy(_._1).mapValues(_.unzip._2)

    val getterPrimitives = for {
      (key, syntaxes) ← propertySyntaxes
      name = key
      outputType = syntaxes.map(_.outputType).reduce(_ | _)
      prim = new GetProperty(reader, key, outputType, widgetContextManager)
    } yield name -> prim

    val setterPrimitives = for {
      (key, syntaxes) ← propertySyntaxes
      name = "SET-" + key
      inputType = syntaxes.map(_.inputType).reduce(_ | _)
      prim = new SetProperty(writer, key, inputType, widgetContextManager)
    } yield name -> prim

    primitives =
      staticPrimitives ++ constructorPrimitives ++
        getterPrimitives ++ setterPrimitives

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

  override def clearAll(): Unit = {
    widgetContextManager.clear()
    writer.clearAll()
  }
}
