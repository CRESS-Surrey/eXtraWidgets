package uk.ac.surrey.xw.api

import java.awt.Component

trait ExtraWidget extends Component {

  val key: WidgetKey
  val stateUpdater: StateUpdater

  val kind: WidgetKind[this.type]

  def init(propertyMap: PropertyMap): Unit =
    for ((propertyKey, value) ← propertyMap)
      setProperty(propertyKey, value)

  def setProperty(
    propertyKey: PropertyKey,
    propertyValue: PropertyValue): Unit =
    for (property ← kind.properties.get(propertyKey))
      property.set(this, propertyValue)

  def updateInState(property: Property[_, this.type]): Unit =
    stateUpdater.set(property.key, this.key, property.get(this))
}
