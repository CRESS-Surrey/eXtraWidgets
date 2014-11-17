package uk.ac.surrey.xw.state

import scala.Option.option2Iterable
import scala.collection.mutable.Publisher

import uk.ac.surrey.xw.api.KindName
import uk.ac.surrey.xw.api.PropertyKey
import uk.ac.surrey.xw.api.PropertyMap
import uk.ac.surrey.xw.api.PropertyValue
import uk.ac.surrey.xw.api.StateUpdater
import uk.ac.surrey.xw.api.Tab
import uk.ac.surrey.xw.api.TabKind
import uk.ac.surrey.xw.api.WidgetKey
import uk.ac.surrey.xw.api.WidgetKind
import uk.ac.surrey.xw.api.XWException
import uk.ac.surrey.xw.api.enrichEither
import uk.ac.surrey.xw.api.normalizeString

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

  val tabPropertyKey = "TAB"
  val kindPropertyKey = "KIND"
  val (tabKindName, orderPropertyKey) = {
    val tabKind = new TabKind[Tab]
    (tabKind.name, tabKind.orderProperty.key)
  }

  def add(widgetKey: WidgetKey, propertyMap: PropertyMap): Unit = {
    val wKey = normalizeString(widgetKey)
    val properties = propertyMap.normalizeKeys
    reader.validateNonEmpty("widget key", wKey).rightOrThrow
    reader.validateUnique("widget key", wKey).rightOrThrow

    val kind = getKind(widgetKey, properties)
    val tabProperty: PropertyMap = kind match {
      case _: TabKind[_] ⇒ Map.empty
      case _ if properties.isDefinedAt(tabPropertyKey) ⇒ Map.empty
      case _ ⇒ Map(tabPropertyKey -> getLastTabKey)
    }
    val allProperties = kind.defaultValues ++ properties ++ tabProperty
    widgetMap += wKey -> allProperties.asMutablePropertyMap
    publish(AddWidget(wKey, allProperties))
  }

  def getKind(widgetKey: WidgetKey, propertyMap: PropertyMap): WidgetKind[_] = {
    val kindName = propertyMap
      .getOrElse(kindPropertyKey, throw XWException(
        "Cannot add widget " + widgetKey + " since its kind is unspecified.",
        XWException(kindPropertyKey + " missing in: " + propertyMap))
      ) match {
        case s: String ⇒ s
        case x ⇒ throw XWException(
          "Expected widget kind to be a string but got " + x + " instead.",
          XWException(kindPropertyKey + " not a string in: " + propertyMap)
        )
      }
    widgetKinds.getOrElse(kindName, throw XWException(
      "Undefined widget kind: " + kindName + ". " +
        "Available kinds are: " + widgetKinds.keys.mkString(", ") + "."))
  }

  def getLastTabKey: WidgetKey =
    (for {
      (widgetKey, properties) ← widgetMap.toSeq
      kindName ← properties.get(kindPropertyKey)
      if kindName == tabKindName
      order ← properties.get(orderPropertyKey)
        .collect { case order: java.lang.Double ⇒ order }
    } yield (widgetKey, order))
      .sorted
      .lastOption
      .map(_._1)
      .getOrElse(throw new XWException("No widget tab has been created yet."))

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
