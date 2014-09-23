package uk.ac.surrey.soc.cress.extrawidgets.plugin.model

import uk.ac.surrey.soc.cress.extrawidgets.plugin.GUIStrings.Data.propertyMustBeNonEmpty
import uk.ac.surrey.soc.cress.extrawidgets.plugin.GUIStrings.Data.propertyMustBeUnique

class Reader(store: Store) {

  def validateNonEmpty(property: PropertyName, value: String) =
    Either.cond(value.nonEmpty, value, propertyMustBeNonEmpty(property))

  def validateUnique(
    property: PropertyName,
    value: PropertyValue,
    filter: collection.Map[PropertyName, PropertyValue] ⇒ Boolean = _ ⇒ true) = {
    val otherValues = for {
      (v, w) ← store
      if filter(w)
    } yield v
    Either.cond(isUnique(value, otherValues), value, propertyMustBeUnique(property, value))
  }

  def isUnique[A](value: A, existingValues: Iterable[A]) =
    !existingValues.exists(_ == value)

}