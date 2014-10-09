package uk.ac.surrey.soc.cress.extrawidgets.api

abstract class PropertyDef[+W <: ExtraWidget](widget: W) {
  type ValueType <: PropertyValue
  def setValue(newValue: ValueType, oldValue: Option[ValueType]): Unit
  def defaultValue: ValueType
  def getValue: ValueType
  def unsetValue(): Unit = setValue(defaultValue, Some(getValue))
}
