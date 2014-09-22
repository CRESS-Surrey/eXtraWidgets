package uk.ac.surrey.soc.cress.extrawidgets.extension

import org.nlogo.api.DefaultClassManager
import org.nlogo.api.ExtensionManager
import org.nlogo.api.PrimitiveManager

import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.Add
import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.Version
import uk.ac.surrey.soc.cress.extrawidgets.plugin.data.ExtraWidgetsData
import uk.ac.surrey.soc.cress.extrawidgets.plugin.data.MutableExtraWidgetsData

class ExtraWidgetsExtension extends DefaultClassManager {

  private var _data: MutableExtraWidgetsData = null
  def data = _data

  override def runOnce(em: ExtensionManager): Unit = {
    _data = ExtraWidgetsData.getOrCreateIn(em)
  }

  def load(primitiveManager: PrimitiveManager): Unit = {
    println("load() " + this)
    val prims = Seq(
      new Version("0.0.0-wip"),
      new Add(data)
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