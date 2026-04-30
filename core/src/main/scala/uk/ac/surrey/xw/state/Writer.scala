package uk.ac.surrey.xw.state

import java.util.concurrent.CopyOnWriteArrayList

import scala.jdk.CollectionConverters.CollectionHasAsScala

import uk.ac.surrey.xw.api.KindName
import uk.ac.surrey.xw.api.PropertyKey
import uk.ac.surrey.xw.api.PropertyMap
import uk.ac.surrey.xw.api.PropertyValue
import uk.ac.surrey.xw.api.State
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
  widgetKinds: Map[KindName, WidgetKind[_]])
  extends Reader(widgetMap)
  with State {

  private val subscribers =
    new CopyOnWriteArrayList[(StateEvent ⇒ Unit, StateEvent ⇒ Boolean)]

  def subscribe(listener: StateEvent ⇒ Unit): Unit =
    subscribe(listener, _ ⇒ true)

  def subscribe(listener: StateEvent ⇒ Unit, filter: StateEvent ⇒ Boolean): Unit =
    subscribers.add(listener -> filter)

  def removeSubscription(listener: StateEvent ⇒ Unit): Unit =
    subscribers.removeIf(entry ⇒ entry._1 == listener)

  private def publish(event: StateEvent): Unit =
    subscribers.asScala.foreach {
      case (listener, filter) if filter(event) ⇒ listener(event)
      case _ ⇒
    }

  private var tabCreationSeq: Seq[WidgetKey] = Seq.empty
  override def tabCreationOrder(tabKey: WidgetKey) =
    tabCreationSeq.indexOf(tabKey)

  def add(widgetKey: WidgetKey, propertyMap: PropertyMap): Unit = {
    val properties = propertyMap.normalizeKeys
    validateNonEmpty("KEY", widgetKey).rightOrThrow
    validateUnique("KEY", widgetKey).rightOrThrow
    val kind = getKind(widgetKey, properties)
    val tabProperty: PropertyMap = kind match {
      case _: TabKind[_] ⇒
        tabCreationSeq = tabCreationSeq :+ widgetKey
        Map.empty
      case _ if properties.isDefinedAt(tabPropertyKey) ⇒
        Map.empty
      case _ ⇒
        Map(tabPropertyKey -> tabCreationSeq.lastOption.getOrElse(
          throw new XWException("There currently are no extra tabs."))
        )
    }
    val keyProperty = Map(keyPropertyKey -> widgetKey)
    val allProperties = kind.defaultValues ++ properties ++ tabProperty ++ keyProperty
    widgetMap += widgetKey -> allProperties.asMutablePropertyMap
    publish(AddWidget(widgetKey, allProperties))
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

  def remove(widgetKey: WidgetKey): Unit = {
    if (get(kindPropertyKey, widgetKey) == tabKindName) {
      // Special case: if we're removing a tab, also
      // remove the widgets on that tab from the widget map
      widgetMap --= widgetMap.collect {
        case (k, ps) if ps.get(tabPropertyKey) == Some(widgetKey) ⇒ k
      }
      tabCreationSeq = tabCreationSeq.filterNot(_ == widgetKey)
    }
    widgetMap -= widgetKey
    publish(RemoveWidget(widgetKey))
  }

  def set(
    propertyKey: PropertyKey,
    widgetKey: WidgetKey,
    propertyValue: PropertyValue): Unit =
    set(propertyKey, widgetKey, propertyValue, fromUI = true)

  def set(
    propertyKey: PropertyKey,
    widgetKey: WidgetKey,
    propertyValue: PropertyValue,
    fromUI: Boolean): Unit = {

    widgetMap.get(widgetKey).orElse {
      if (!fromUI)
        throw XWException("Widget " + widgetKey + " does not exist.")
      else
        None
    }.foreach { propertyMap =>
      val pKey = normalizeString(propertyKey)
      val oldValue = propertyMap.get(pKey)
      if (Some(propertyValue) != oldValue) {
        propertyMap += pKey -> propertyValue
        publish(SetProperty(widgetKey, pKey, propertyValue, fromUI))
      }
    }
  }

  def clearAll(): Unit = {
    widgetKeyVector.sortBy { k ⇒ // tabs last
      propertyMap(k).right.toOption
        .flatMap(_.get("KIND"))
        .map(_.toString).map(normalizeString) == Some(tabKindName)
    }.foreach(remove)
  }
}
