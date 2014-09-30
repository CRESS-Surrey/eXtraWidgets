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

  def validateNonEmpty(property: PropertyName, value: String) =
    Either.cond(value.nonEmpty, value, propertyMustBeNonEmpty(property))

  def validateUnique(
    property: PropertyName,
    value: PropertyValue,
    filter: collection.Map[PropertyName, PropertyValue] ⇒ Boolean = _ ⇒ true) = {
    val otherValues = for {
      (v, w) ← widgetMap
      if filter(w)
    } yield v
    Either.cond(isUnique(value, otherValues), value, propertyMustBeUnique(property, value))
  }

  def isUnique[A](value: A, existingValues: Iterable[A]) =
    !existingValues.exists(_ == value)

}
