package uk.ac.surrey.xw.api

trait State {
  def set(
    propertyKey: PropertyKey,
    widgetKey: WidgetKey,
    propertyValue: PropertyValue): Unit
  def tabCreationOrder(tabKey: WidgetKey): Int
}
