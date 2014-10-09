package uk.ac.surrey.soc.cress.extrawidgets.api

trait PropertyDef[W] {
  val name: String
  def setValueFor(w: W, newValue: PropertyValue, oldValue: Option[PropertyValue]): Unit
  def unsetValueFor(w: W)
}
