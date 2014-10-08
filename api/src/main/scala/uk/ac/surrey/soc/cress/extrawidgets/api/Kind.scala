package uk.ac.surrey.soc.cress.extrawidgets.api

import org.nlogo.window.GUIWorkspace

trait Kind {
  type W <: ExtraWidget
  val name: String
  def newInstance(key: WidgetKey, properties: PropertyMap, ws: GUIWorkspace): ExtraWidget
}
