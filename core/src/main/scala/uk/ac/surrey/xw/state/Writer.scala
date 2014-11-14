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
import uk.ac.surrey.xw.api.TabKind
import uk.ac.surrey.xw.api.Tab

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

  val kindPropertyKey = "KIND"
  val orderPropertyKey = "ORDER"
  val tabPropertyKey = "TAB"
  val tabKindName = new TabKind[Tab].name

  def add(widgetKey: WidgetKey, propertyMap: PropertyMap): Unit = {
    val wKey = normalizeString(widgetKey)
    val properties = propertyMap.normalizeKeys
    reader.validateNonEmpty("widget key", wKey).rightOrThrow
    reader.validateUnique("widget key", wKey).rightOrThrow

    val kind = getKind(widgetKey, properties)
    println(kind)
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
      order = properties.get(orderPropertyKey) match {
        case Some(n: java.lang.Double) ⇒ n
        case _ ⇒ Double.box(0)
      }
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
