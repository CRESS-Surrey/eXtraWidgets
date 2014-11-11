package uk.ac.surrey.xw.api

trait StateUpdater {
  def set(
    propertyKey: PropertyKey,
    widgetKey: WidgetKey,
    propertyValue: PropertyValue): Unit
}
