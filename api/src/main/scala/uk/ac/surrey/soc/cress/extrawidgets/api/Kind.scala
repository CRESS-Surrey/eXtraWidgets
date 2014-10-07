package uk.ac.surrey.soc.cress.extrawidgets.api

import org.nlogo.window.GUIWorkspace

trait Kind {
  type W
  val name: String
  def newInstance(key: WidgetKey, properties: PropertyMap, ws: GUIWorkspace): W
}
