package uk.ac.surrey.soc.cress.extrawidgets.extension

import org.nlogo.api.DefaultClassManager
import org.nlogo.api.ExtensionManager
import org.nlogo.api.PrimitiveManager

import uk.ac.surrey.soc.cress.extrawidgets.plugin.model._

import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.Add
import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.Version

class ExtraWidgetsExtension extends DefaultClassManager {

  private var store: MutableStore = null
  private var writer: Writer = null
  private var reader: Reader = null

  override def runOnce(em: ExtensionManager): Unit = {
    store = getOrCreateStoreIn(em)
    writer = new Writer(store)
    reader = new Reader(store)
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