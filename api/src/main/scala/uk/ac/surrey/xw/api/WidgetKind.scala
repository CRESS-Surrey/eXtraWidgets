package uk.ac.surrey.xw.api

import org.nlogo.window.GUIWorkspace

abstract class WidgetKind[W] {
  val newWidget: (WidgetKey, StateUpdater, GUIWorkspace) ⇒ ExtraWidget
  val name: String
  def pluralName = name + "S"
  def defaultProperty: Option[Property[Any, W]]
  def propertySet: Set[Property[Any, W]]
  lazy val properties =
    propertySet.map(p ⇒ p.key -> p).toMap
  lazy val defaultValues = properties.mapValues(
    _.defaultValue.asInstanceOf[AnyRef]
  )
}
