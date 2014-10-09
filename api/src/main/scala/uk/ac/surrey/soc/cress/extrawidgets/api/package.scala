package uk.ac.surrey.soc.cress.extrawidgets

import scala.collection.Map
import scala.collection.immutable

package object api {

  val pluginName = "eXtraWidgets"

  type Key = String
  type WidgetKey = Key
  type PropertyKey = Key
  type PropertyValue = AnyRef

  type PropertyMap = immutable.Map[PropertyKey, PropertyValue]
  type WidgetMap = immutable.Map[WidgetKey, PropertyMap]

}
