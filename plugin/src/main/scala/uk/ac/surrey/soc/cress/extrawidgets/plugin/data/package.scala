package uk.ac.surrey.soc.cress.extrawidgets.plugin

import scala.collection.mutable.{ Map ⇒ MMap }

package object data {
  type WidgetName = String
  type PropertyName = String
  type PropertyValue = Any
  type Store = MMap[WidgetName, MMap[PropertyName, PropertyValue]]
}