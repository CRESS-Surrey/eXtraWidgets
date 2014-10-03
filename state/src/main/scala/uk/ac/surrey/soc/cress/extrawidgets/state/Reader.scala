package uk.ac.surrey.soc.cress.extrawidgets.state

import org.nlogo.api.SimpleChangeEvent
import org.nlogo.api.SimpleChangeEventPublisher

import Strings.propertyMustBeNonEmpty
import Strings.propertyMustBeUnique

class Reader(
  val widgetMap: WidgetMap,
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

}
