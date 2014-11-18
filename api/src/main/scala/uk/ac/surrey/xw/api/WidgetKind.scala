package uk.ac.surrey.xw.api

import org.nlogo.window.GUIWorkspace

abstract class WidgetKind[W <: ExtraWidget] {
  val newWidget: (WidgetKey, StateUpdater, GUIWorkspace) ⇒ ExtraWidget
  val name: String
  def pluralName = name + "S"
  def defaultProperty: Option[Property[Any, W]]
  val kindProperty = new StringProperty[W]("KIND", None, _.kind.name)
  def propertySet: Set[Property[Any, W]] = Set(kindProperty)
  lazy val properties =
    propertySet.map(p ⇒ p.key -> p).toMap
  lazy val defaultValues = properties.mapValues(
    _.defaultValue.asInstanceOf[AnyRef]
  )
}
