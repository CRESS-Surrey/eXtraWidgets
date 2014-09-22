package uk.ac.surrey.soc.cress.extrawidgets.extension

import org.nlogo.api.ClassManager
import org.nlogo.api.ExtensionManager
import org.nlogo.api.ExtensionObject
import org.nlogo.api.ImportErrorHandler
import org.nlogo.api.PrimitiveManager

import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.Version

class ExtraWidgetsExtension extends ClassManager {

  override def runOnce(em: ExtensionManager): Unit = {
    println("runOnce() " + this)
  }

  def load(primitiveManager: PrimitiveManager): Unit = {
    println("load() " + this)
    val prims = Seq(new Version("0.0.0-wip"))
    for (p ← prims) primitiveManager.addPrimitive(p.primitiveName, p)
  }

  override def unload(em: ExtensionManager): Unit = {
    println("unload() " + this)
  }

  override def clearAll(): Unit = {
    println("unload clearAll() " + this)
  }

  override def exportWorld: java.lang.StringBuilder = new java.lang.StringBuilder

  override def importWorld(lines: java.util.List[Array[String]], reader: ExtensionManager, handler: ImportErrorHandler) {}

  override def readExtensionObject(em: ExtensionManager, typeName: String, value: String): ExtensionObject =
    throw new IllegalStateException("readExtensionObject not implemented for " + this)

  override def additionalJars: java.util.List[String] = new java.util.ArrayList[String]

}