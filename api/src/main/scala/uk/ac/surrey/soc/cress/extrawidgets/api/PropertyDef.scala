package uk.ac.surrey.soc.cress.extrawidgets.api

abstract class PropertyDef[W](val w: W) {
  def set(newValue: PropertyValue, oldValue: Option[PropertyValue]): Unit
  def unset
}
