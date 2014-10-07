package uk.ac.surrey.soc.cress.extrawidgets.api

trait PropertyDef[W <: ExtraWidget[W]] {
  val valueType: Int // to be used for creating setters in extension
  def set(w: W, newValue: PropertyValue, oldValue: Option[PropertyValue]): Unit
  def unset(w: W)
}
