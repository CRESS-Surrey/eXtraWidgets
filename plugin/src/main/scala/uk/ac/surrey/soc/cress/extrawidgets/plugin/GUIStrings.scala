package uk.ac.surrey.soc.cress.extrawidgets.plugin

object GUIStrings {
  object TabsManager {
    val TabNameQuestion = "How would you like to name your new tab?"
    val DefaultTabName = "New Tab"
    val InvalidTabName = "Invalid tab name"
  }
  object Data {
    val TabNameMustBeNonEmpty = "You must enter a name for your tab."
    def nameMustBeUnique(kind: String, name: String) =
      "There is already a " + kind + " named \"" + name + "\". The name must be unique."
  }
  object ToolsMenu {
    val CreateTab = "Create Extra Widgets Tab..."
  }
}