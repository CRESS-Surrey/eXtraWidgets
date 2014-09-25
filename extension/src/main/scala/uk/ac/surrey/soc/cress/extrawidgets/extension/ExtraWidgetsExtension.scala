package uk.ac.surrey.soc.cress.extrawidgets.extension

import org.nlogo.api.DefaultClassManager
import org.nlogo.api.ExtensionManager
import org.nlogo.api.PrimitiveManager

import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.Add
import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.Version
import uk.ac.surrey.soc.cress.extrawidgets.plugin.model.Reader
import uk.ac.surrey.soc.cress.extrawidgets.plugin.model.Writer
import uk.ac.surrey.soc.cress.extrawidgets.plugin.model.getOrCreateModel

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
      new Version("0.0.0-wip"),
      new Add(writer)
    )
    for (p ← prims) primitiveManager.addPrimitive(p.primitiveName, p)
  }

  override def unload(em: ExtensionManager): Unit = {
    println("unload() " + this)
  }

  override def clearAll(): Unit = {
    println("unload clearAll() " + this)
  }

}