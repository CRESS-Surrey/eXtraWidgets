package uk.ac.surrey.soc.cress.extrawidgets.api

import java.awt.Component

trait ExtraWidget extends Component {

  val key: WidgetKey
  val stateUpdater: StateUpdater

  private var _propertyMap: PropertyMap = Map.empty

  final def propertyMap = _propertyMap

  /* Let's be careful not to access the following lazy vals
   * in this trait's constructor because the property fields
   * in `this` won't be initialised yet. NP 2014-10-10.
   */
  lazy val kind = new WidgetKind(this.getClass)
  lazy val propertyDefs: Map[PropertyKey, PropertyDef[_ <: ExtraWidget, _]] =
    kind.propertyDefs(this)
  lazy val propertyKeys: Map[PropertyDef[_ <: ExtraWidget, _], PropertyKey] =
    propertyDefs.map(_.swap)

  def init(newPropertyMap: PropertyMap): Unit = {
    _propertyMap = newPropertyMap
    for {
      propertyKey ← kind.propertyKeys
      if !propertyMap.contains(key)
      prop ← propertyDefs.get(propertyKey)
    } prop.setToDefault()

    for {
      (propertyKey, propertyValue) ← newPropertyMap
      prop ← propertyDefs.get(propertyKey)
    } prop.setValue(propertyValue)
  }

  def setProperty(
    propertyKey: PropertyKey,
    propertyValue: PropertyValue): Unit = {
    for {
      prop ← propertyDefs.get(propertyKey)
    } prop.setValue(propertyValue)
  }

  def updatePropertyInState(propertyDef: PropertyDef[_ <: ExtraWidget, _]): Unit =
    stateUpdater.set(
      propertyKeys(propertyDef), key,
      propertyDef.getter().asInstanceOf[AnyRef])
}
