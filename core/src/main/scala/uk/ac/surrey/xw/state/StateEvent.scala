package uk.ac.surrey.xw.state

import uk.ac.surrey.xw.api.PropertyKey
import uk.ac.surrey.xw.api.PropertyMap
import uk.ac.surrey.xw.api.PropertyValue
import uk.ac.surrey.xw.api.WidgetKey

sealed abstract trait StateEvent

case class AddWidget(
  val widgetKey: WidgetKey,
  val propertyMap: PropertyMap)
  extends StateEvent

case class SetProperty(
  val widgetKey: WidgetKey,
  val propertyKey: PropertyKey,
  val propertyValue: PropertyValue,
  val fromUI: Boolean)
  extends StateEvent

case class RemoveWidget(
  val widgetKey: WidgetKey)
  extends StateEvent
