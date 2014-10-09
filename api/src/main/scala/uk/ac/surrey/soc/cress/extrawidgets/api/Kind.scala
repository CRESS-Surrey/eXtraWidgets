package uk.ac.surrey.soc.cress.extrawidgets.api

import org.nlogo.window.GUIWorkspace
import uk.ac.surrey.soc.cress.extrawidgets.api.util._

trait Kind {
  type W <: ExtraWidget
  val name: String
  def newInstance(key: WidgetKey, properties: PropertyMap, ws: GUIWorkspace): W
  def propertyDefs: Seq[PropertyDef[_ >: W]]
  lazy val propertyDefMap: Map[PropertyKey, PropertyDef[_ >: W]] =
    propertyDefs.map(pd ⇒ normalizeKey(pd.name) -> pd).toMap
}
