package uk.ac.surrey.soc.cress.extrawidgets.state

object Strings {
  def propertyMustBeNonEmpty(property: PropertyKey) =
    "Property \"" + property + "\" must not be empty."
  def propertyMustBeUnique(property: PropertyKey, value: PropertyValue) =
    "There is already a widget with value \"" + value + "\" for property \"" + property + "\"."
}
