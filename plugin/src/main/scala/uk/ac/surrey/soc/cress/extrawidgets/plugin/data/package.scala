package uk.ac.surrey.soc.cress.extrawidgets.plugin

import scala.collection.mutable.{ ConcurrentMap ⇒ CMap }

package object data {
  type Kind = String
  type WidgetName = String
  type PropertyName = String
  type PropertyValue = Any
  type Store = CMap[WidgetName, CMap[PropertyName, PropertyValue]]
}