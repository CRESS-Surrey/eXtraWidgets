package uk.ac.surrey.xw.state

import uk.ac.surrey.xw.api.PropertyKey
import uk.ac.surrey.xw.api.PropertyValue

object Strings {
  def propertyMustBeNonEmpty(property: PropertyKey) =
    "Property \"" + property + "\" must not be empty."
  def propertyMustBeUnique(property: PropertyKey, value: PropertyValue) =
    "There is already a widget with value \"" + value + "\" for property \"" + property + "\"."
}
