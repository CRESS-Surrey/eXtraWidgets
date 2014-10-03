package uk.ac.surrey.soc.cress.extrawidgets.extension

import org.nlogo.api.DefaultClassManager
import org.nlogo.api.ExtensionManager
import org.nlogo.api.PrimitiveManager

import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.Add
import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.Get
import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.Properties
import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.PropertyKeys
import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.Remove
import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.Set
import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.Version
import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.WidgetKeys
import uk.ac.surrey.soc.cress.extrawidgets.state.Reader
import uk.ac.surrey.soc.cress.extrawidgets.state.Writer
import uk.ac.surrey.soc.cress.extrawidgets.state.getOrCreateModel

class ExtraWidgetsExtension extends DefaultClassManager {

  private var writer: Writer = null
  private var reader: Reader = null

  override def runOnce(em: ExtensionManager): Unit = {
    val tuple: (Reader, Writer) = getOrCreateModel(em)
    reader = tuple._1
    writer = tuple._2
  }

  def load(primitiveManager: PrimitiveManager): Unit = {
    println("load() " + this)
    val prims = Seq(
      "version" -> new Version("0.0.0-wip"),
      "add" -> new Add(writer),
      "remove" -> new Remove(writer),
      "set" -> new Set(writer),
      "get" -> new Get(reader),
      "widget-keys" -> new WidgetKeys(reader),
      "property-keys" -> new PropertyKeys(reader),
      "properties" -> new Properties(reader)
    )
    for ((name, prim) ← prims) primitiveManager.addPrimitive(name, prim)
  }

  override def unload(em: ExtensionManager): Unit = {
    println("unload() " + this)
  }

  override def clearAll(): Unit = {
    println("unload clearAll() " + this)
  }

}
