package uk.ac.surrey.soc.cress.extrawidgets.state

import org.nlogo.api.SimpleChangeEvent
import org.nlogo.api.SimpleChangeEventPublisher

import Strings.propertyMustBeNonEmpty
import Strings.propertyMustBeUnique
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyKey
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyMap
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyValue
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKey

class Reader(
  widgetMap: MutableWidgetMap, // reader should never expose any part of this
  publisher: SimpleChangeEventPublisher) {

  def onChange[A](f: ⇒ A): Unit = {
    val sub = new SimpleChangeEventPublisher#Sub {
      publisher.subscribe(this)
      override def notify(
        pub: SimpleChangeEventPublisher#Pub,
        event: SimpleChangeEvent.type) {
        f
      }
    }
  }

  def validateNonEmpty(propertyKey: PropertyKey, value: String) =
    Either.cond(value.nonEmpty, value, propertyMustBeNonEmpty(propertyKey))

  def validateUnique(
    propertyKey: PropertyKey,
    value: PropertyValue,
    filter: collection.Map[PropertyKey, PropertyValue] ⇒ Boolean = _ ⇒ true) = {
    val otherValues = for {
      (v, w) ← widgetMap
      if filter(w)
    } yield v
    Either.cond(isUnique(value, otherValues), value, propertyMustBeUnique(propertyKey, value))
  }

  def isUnique[A](value: A, existingValues: Iterable[A]) =
    !existingValues.exists(_ == value)

  def get(propertyKey: PropertyKey, widgetKey: WidgetKey): Either[String, PropertyValue] =
    for {
      propertyMap ← mutablePropertyMap(widgetKey).right
      propertyValue ← propertyMap.get(normalizeKey(propertyKey)).toRight(
        "Property " + propertyKey + " does not exist for widget " + widgetKey + ".").right
    } yield propertyValue

  def widgetKeySet: Set[WidgetKey] = widgetMap.keys.toSet

  def widgetKeyVector: Vector[WidgetKey] = Vector() ++ widgetMap.keys

  private def mutablePropertyMap(widgetKey: WidgetKey): Either[String, MutablePropertyMap] =
    widgetMap.get(normalizeKey(widgetKey)).toRight(
      "Widget " + widgetKey + " does not exist in " + widgetMap)

  def propertyMap(widgetKey: WidgetKey): Either[String, PropertyMap] =
    mutablePropertyMap(widgetKey).right.map(_.toMap)

  def contains(widgetKey: WidgetKey) = widgetMap.contains(normalizeKey(widgetKey))

  def propertyKeyVector(widgetKey: WidgetKey): Either[String, Vector[PropertyKey]] =
    mutablePropertyMap(widgetKey).right.map(Vector() ++ _.keysIterator)

  def properties(widgetKey: WidgetKey): Either[String, Vector[(PropertyKey, PropertyValue)]] =
    mutablePropertyMap(widgetKey).right.map(Vector() ++ _.iterator)
}
