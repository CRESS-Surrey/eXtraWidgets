package uk.ac.surrey.xw.state

import scala.collection.mutable.Publisher
import uk.ac.surrey.xw.api.PropertyKey
import uk.ac.surrey.xw.api.PropertyMap
import uk.ac.surrey.xw.api.PropertyValue
import uk.ac.surrey.xw.api.StateUpdater
import uk.ac.surrey.xw.api.WidgetKey
import uk.ac.surrey.xw.api.XWException
import uk.ac.surrey.xw.api.enrichEither
import uk.ac.surrey.xw.api.normalizeString
import uk.ac.surrey.xw.api.XWException
import uk.ac.surrey.xw.api.WidgetKind
import uk.ac.surrey.xw.api.KindName

/**
 *  This is the only class that should <em>ever</em> write to the MutableWidgetMap.
 */
class Writer(
  widgetMap: MutableWidgetMap,
  widgetKinds: Map[KindName, WidgetKind[_]],
  val reader: Reader)
  extends Publisher[StateEvent]
  with StateUpdater {

  override type Pub = Publisher[StateEvent]

  def add(widgetKey: WidgetKey, propertyMap: PropertyMap): Unit = {
    val wKey = normalizeString(widgetKey)
    val properties = propertyMap.normalizeKeys
    reader.validateNonEmpty("widget key", wKey).rightOrThrow
    reader.validateUnique("widget key", wKey).rightOrThrow
    val kindName = properties
      .getOrElse("KIND", throw XWException(
        "Cannot add widget " + wKey + " since its kind is unspecified.",
        XWException("KIND missing in: " + properties))
      ) match {
        case s: String ⇒ s
        case x ⇒ throw XWException(
          "Expected widget kind to be a string but got " + x + " instead.",
          XWException("KIND not a string in: " + properties)
        )
      }
    val kind = widgetKinds.getOrElse(kindName, throw XWException(
      "Undefined widget kind: " + kindName + ". " +
        "Available kinds are: " + widgetKinds.keys.mkString(", ") + "."))

    val allProperties = kind.defaultValues ++ properties
    widgetMap += wKey -> allProperties.asMutablePropertyMap
    publish(AddWidget(wKey, allProperties))
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
