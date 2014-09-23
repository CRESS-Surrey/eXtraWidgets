package uk.ac.surrey.soc.cress.extrawidgets.plugin

import data._

object GUIStrings {
  object TabsManager {
    val TabNameQuestion = "How would you like to name your new tab?"
    val DefaultTabName = "New Tab"
    val InvalidTabName = "Invalid tab name"
  }
  object Data {
    def propertyMustBeNonEmpty(property: PropertyName) =
      "Property \"" + property + "\" must not be empty."
    def propertyMustBeUnique(property: PropertyName, value: PropertyValue) =
      "There is already a widget with value \"" + value + "\" for property \"" + property + "\"."
  }
  object ToolsMenu {
    val CreateTab = "Create Extra Widgets Tab..."
  }
}