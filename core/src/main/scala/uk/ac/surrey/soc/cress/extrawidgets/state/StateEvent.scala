package uk.ac.surrey.soc.cress.extrawidgets.state

import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKey
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyMap
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyKey
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyValue

sealed abstract trait StateEvent

case class AddWidget(
  val widgetKey: WidgetKey,
  val propertyMap: PropertyMap)
  extends StateEvent

case class SetProperty(
  val widgetKey: WidgetKey,
  val propertyKey: PropertyKey,
  val propertyValue: PropertyValue)
  extends StateEvent

case class RemoveWidget(
  val widgetKey: WidgetKey)
  extends StateEvent
