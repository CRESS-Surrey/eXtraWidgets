package uk.ac.surrey.soc.cress.extrawidgets.api

trait StateUpdater {
  def set(
    propertyKey: PropertyKey,
    widgetKey: WidgetKey,
    propertyValue: PropertyValue): Unit
}
