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

  def add(widgetKey: WidgetKey, properties: PropertyMap): Either[String, Unit] = {
    val wKey = normalizeKey(widgetKey)
    for {
      _ ← reader.validateNonEmpty("widget key", wKey).right
      _ ← reader.validateUnique("widget key", wKey).right
    } yield {
      widgetMap += wKey -> properties.asMutablePropertyMap
      publisher.publish()
    }
  }

  def remove(widgetKey: WidgetKey): Unit = {
    widgetMap -= normalizeKey(widgetKey)
    publisher.publish()
  }

  def set(
    propertyKey: PropertyKey,
    widgetKey: WidgetKey,
    propertyValue: PropertyValue): Either[String, Unit] = {
    val wKey = normalizeKey(widgetKey)
    for {
      propertyMap ← widgetMap.get(wKey)
        .toRight("Widget " + wKey + " does not exist.").right
    } yield {
      propertyMap += normalizeKey(propertyKey) -> propertyValue
      publisher.publish()
    }
  }
}
