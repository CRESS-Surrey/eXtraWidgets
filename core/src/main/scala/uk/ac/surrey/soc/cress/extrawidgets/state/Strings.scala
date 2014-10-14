package uk.ac.surrey.soc.cress.extrawidgets.state

import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyKey
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyValue

object Strings {
  def propertyMustBeNonEmpty(property: PropertyKey) =
    "Property \"" + property + "\" must not be empty."
  def propertyMustBeUnique(property: PropertyKey, value: PropertyValue) =
    "There is already a widget with value \"" + value + "\" for property \"" + property + "\"."
}
