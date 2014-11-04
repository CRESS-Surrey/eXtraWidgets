package uk.ac.surrey.soc.cress.extrawidgets.api

import java.awt.Component

trait ExtraWidget extends Component {

  val key: WidgetKey
  val stateUpdater: StateUpdater

  /* Let's be careful not to access the following lazy vals
   * in this trait's constructor because the property fields
   * in `this` won't be initialised yet. NP 2014-10-10.
   */
  lazy val kind = new WidgetKind(this.getClass)
  lazy val propertyDefs: Map[PropertyKey, PropertyDef[_]] =
    kind.propertyDefs(this)
  lazy val propertyKeys: Map[PropertyDef[_], PropertyKey] =
    propertyDefs.map(_.swap)

  /**
   *  Initialize the widget by setting all its properties.
   *  The properties we got in the map are set to the provided
   *  value. The other properties get "set" to their current values,
   *  to ensure that these values are written back to the State.
   */
  def init(propertyMap: PropertyMap): Unit = {
    println(propertyMap)
    for {
      propertyKey ← kind.propertyKeys
      propertyDef ← propertyDefs.get(propertyKey)
      propertyValue = propertyMap.getOrElse(propertyKey, propertyDef.get)
    } setProperty(propertyDef, propertyValue)
  }

  def setProperty(
    propertyKey: PropertyKey,
    propertyValue: PropertyValue): Unit =
    propertyDefs.get(propertyKey).foreach(setProperty(_, propertyValue))

  def setProperty(
    propertyDef: PropertyDef[_],
    propertyValue: PropertyValue): Unit = {
    propertyDef.set(propertyValue)
    updateInState(propertyDef)
  }

  def updateInState(propertyDef: PropertyDef[_]): Unit =
    stateUpdater.set(
      propertyKeys(propertyDef), key,
      propertyDef.get)
}
