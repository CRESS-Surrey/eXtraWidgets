package uk.ac.surrey.soc.cress.extrawidgets.state

import scala.collection.mutable.Publisher

import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyKey
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyMap
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyValue
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKey
import uk.ac.surrey.soc.cress.extrawidgets.api.XWException
import uk.ac.surrey.soc.cress.extrawidgets.api.enrichOption
import uk.ac.surrey.soc.cress.extrawidgets.api.normalizeString

/**
 *  This is the only class that should <em>ever</em> write to the MutableWidgetMap.
 */
class Writer(
  widgetMap: MutableWidgetMap,
  reader: Reader)
  extends Publisher[StateEvent] {

  override type Pub = Publisher[StateEvent]

  def add(widgetKey: WidgetKey, propertyMap: PropertyMap): Either[XWException, Unit] = {
    val wKey = normalizeString(widgetKey)
    val properties = propertyMap.normalizeKeys
    for {
      _ ← reader.validateNonEmpty("widget key", wKey).right
      _ ← reader.validateUnique("widget key", wKey).right
    } yield {
      widgetMap += wKey -> properties.asMutablePropertyMap
      publish(AddWidget(wKey, properties))
    }
  }

  def remove(widgetKey: WidgetKey): Unit = {
    val wKey = normalizeString(widgetKey)
    widgetMap -= wKey
    publish(RemoveWidget(wKey))
  }

  def set(
    propertyKey: PropertyKey,
    widgetKey: WidgetKey,
    propertyValue: PropertyValue): Either[XWException, Unit] = {
    val wKey = normalizeString(widgetKey)
    for {
      propertyMap ← widgetMap.get(wKey).orException(
        "Widget " + wKey + " does not exist.").right
    } yield {
      val pKey = normalizeString(propertyKey)
      propertyMap += pKey -> propertyValue
      publish(SetProperty(wKey, pKey, propertyValue))
    }
  }
}
