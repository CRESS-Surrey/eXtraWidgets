package uk.ac.surrey.soc.cress.extrawidgets

import scala.collection.Map

package object api {

  val pluginName = "eXtraWidgets"

  type WidgetKind = String
  type WidgetKey = String
  type PropertyKey = String
  type PropertyValue = AnyRef

  type PropertyMap = Map[PropertyKey, PropertyValue]
  type WidgetMap = Map[WidgetKey, PropertyMap]

}
