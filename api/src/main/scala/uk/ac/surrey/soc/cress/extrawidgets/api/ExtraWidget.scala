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
  lazy val properties: Map[PropertyKey, Property[_]] =
    kind.properties(this)
  lazy val propertyKeys: Map[Property[_], PropertyKey] =
    properties.map(_.swap)

  /**
   *  Initialize the widget by setting all its properties.
   *  The properties we got in the map are set to the provided
   *  value. The other properties get "set" to their current values,
   *  to ensure that these values are written back to the State.
   */
  def init(propertyMap: PropertyMap): Unit = {
    println(propertyMap)
    for {
      propertyKey ← propertyKeys.values
      property ← properties.get(propertyKey)
      propertyValue = propertyMap.getOrElse(propertyKey, property.get)
    } setProperty(property, propertyValue)
  }

  def setProperty(
    propertyKey: PropertyKey,
    propertyValue: PropertyValue): Unit =
    properties.get(propertyKey).foreach(setProperty(_, propertyValue))

  def setProperty(
    property: Property[_],
    propertyValue: PropertyValue): Unit = {
    property.set(propertyValue)
    updateInState(property)
  }

  def updateInState(property: Property[_]): Unit =
    stateUpdater.set(
      propertyKeys(property), key,
      property.get)
}
