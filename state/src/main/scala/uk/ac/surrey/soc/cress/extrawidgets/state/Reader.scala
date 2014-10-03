package uk.ac.surrey.soc.cress.extrawidgets.state

import org.nlogo.api.SimpleChangeEvent
import org.nlogo.api.SimpleChangeEventPublisher

import Strings.propertyMustBeNonEmpty
import Strings.propertyMustBeUnique
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyKey
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyValue
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKey
import collection.immutable

class Reader(
  widgetMap: MutableWidgetMap, // reader should never expose any part of this
  publisher: SimpleChangeEventPublisher) {

  def onChange[A](block: ⇒ A): Unit = {
    val sub = new SimpleChangeEventPublisher#Sub {
      publisher.subscribe(this)
      override def notify(
        pub: SimpleChangeEventPublisher#Pub,
        event: SimpleChangeEvent.type) {
        block
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
      propertyMap ← widgetMap.get(widgetKey).toRight(
        "Widget \"" + widgetKey + "\" does not exist.").right
      propertyValue ← propertyMap.get(propertyKey).toRight(
        "Property \"" + propertyKey + "\" " +
          "does not exist for widget \"" + widgetKey + "\".").right
    } yield propertyValue

  def widgetKeys: Set[WidgetKey] = widgetMap.keys.toSet

  def propertyMap(widgetKey: WidgetKey) = widgetMap.get(widgetKey).map(_.toImmutable)

  def contains(widgetKey: WidgetKey) = widgetMap.contains(widgetKey)

}
