package uk.ac.surrey.soc.cress.extrawidgets.api

import org.nlogo.window.GUIWorkspace

trait Kind[W] {
  val name: String
  def propertyDefs: Map[PropertyKey, PropertyDef[_ >: W]]
  def newInstance(key: WidgetKey, properties: PropertyMap, ws: GUIWorkspace): W
}
