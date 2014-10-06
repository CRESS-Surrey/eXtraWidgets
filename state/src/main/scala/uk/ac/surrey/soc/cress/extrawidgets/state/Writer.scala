package uk.ac.surrey.soc.cress.extrawidgets.state

import org.nlogo.api.SimpleChangeEventPublisher
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyKey
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyValue
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKey
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKind
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyMap

/**
 *  This is the only class that should <em>ever</em> write to the MutableWidgetMap.
 */
class Writer(
  widgetMap: MutableWidgetMap,
  publisher: SimpleChangeEventPublisher,
  reader: Reader) {

  def add(widgetKey: WidgetKey, properties: PropertyMap): Either[String, Unit] =
    for {
      _ ← reader.validateNonEmpty("widget key", widgetKey).right
      _ ← reader.validateUnique("widget key", widgetKey).right
    } yield {
      widgetMap += widgetKey -> properties.asMutablePropertyMap
      publisher.publish()
    }

  def remove(widgetKey: WidgetKey): Unit = {
    widgetMap -= widgetKey
    publisher.publish()
  }

  def set(
    propertyKey: PropertyKey,
    widgetKey: WidgetKey,
    propertyValue: PropertyValue): Either[String, Unit] =
    for {
      propertyMap ← widgetMap.get(widgetKey)
        .toRight("Widget \"" + widgetKey + "\" does not exist.").right
    } yield {
      propertyMap += propertyKey -> propertyValue
      publisher.publish()
    }

}
