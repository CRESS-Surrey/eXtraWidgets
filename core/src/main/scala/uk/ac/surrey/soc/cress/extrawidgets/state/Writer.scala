package uk.ac.surrey.soc.cress.extrawidgets.state

import scala.collection.mutable.Publisher

import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyKey
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyMap
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyValue
import uk.ac.surrey.soc.cress.extrawidgets.api.StateUpdater
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKey
import uk.ac.surrey.soc.cress.extrawidgets.api.XWException
import uk.ac.surrey.soc.cress.extrawidgets.api.enrichEither
import uk.ac.surrey.soc.cress.extrawidgets.api.normalizeString

/**
 *  This is the only class that should <em>ever</em> write to the MutableWidgetMap.
 */
class Writer(
  widgetMap: MutableWidgetMap,
  val reader: Reader)
  extends Publisher[StateEvent]
  with StateUpdater {

  override type Pub = Publisher[StateEvent]

  def add(widgetKey: WidgetKey, propertyMap: PropertyMap): Unit = {
    val wKey = normalizeString(widgetKey)
    val properties = propertyMap.normalizeKeys
    reader.validateNonEmpty("widget key", wKey).rightOrThrow
    reader.validateUnique("widget key", wKey).rightOrThrow
    widgetMap += wKey -> properties.asMutablePropertyMap
    publish(AddWidget(wKey, properties))
  }

  def remove(widgetKey: WidgetKey): Unit = {
    val wKey = normalizeString(widgetKey)
    widgetMap -= wKey
    publish(RemoveWidget(wKey))
  }

  def set(
    propertyKey: PropertyKey,
    widgetKey: WidgetKey,
    propertyValue: PropertyValue): Unit =
    set(propertyKey, widgetKey, propertyValue, false)

  def set(
    propertyKey: PropertyKey,
    widgetKey: WidgetKey,
    propertyValue: PropertyValue,
    publishEvent: Boolean): Unit = {

    val wKey = normalizeString(widgetKey)
    val propertyMap = widgetMap.getOrElse(wKey,
      throw XWException("Widget " + wKey + " does not exist."))
    val pKey = normalizeString(propertyKey)
    val oldValue = propertyMap.get(pKey)
    if (Some(propertyValue) != oldValue) {
      propertyMap += pKey -> propertyValue
      println("(" + Thread.currentThread().getName() + ") " +
        wKey + "/" + pKey + " := " + propertyValue)
      if (publishEvent) publish(SetProperty(wKey, pKey, propertyValue))
    }
  }

  def clearAll() {
    reader.widgetKeyVector.sortBy { k ⇒ // tabs last
      reader.propertyMap(k).right.toOption
        .flatMap(_.get("KIND"))
        .map(_.toString).map(normalizeString) == Some("TAB")
    }.foreach(remove)
  }
}
