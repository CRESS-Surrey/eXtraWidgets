package uk.ac.surrey.soc.cress.extrawidgets.extension

import org.nlogo.api.DefaultClassManager
import org.nlogo.api.PrimitiveManager
import uk.ac.surrey.soc.cress.extrawidgets.extension.prim.Version

class ExtraWidgetsExtension extends DefaultClassManager {

  def load(primitiveManager: PrimitiveManager): Unit = {
    val prims = Seq(new Version("0.0.0-wip"))
    for (p <- prims) primitiveManager.addPrimitive(p.primitiveName, p)
  }

}