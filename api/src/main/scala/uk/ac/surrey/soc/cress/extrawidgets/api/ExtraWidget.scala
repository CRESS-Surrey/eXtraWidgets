package uk.ac.surrey.soc.cress.extrawidgets.api

import java.awt.Component

trait ExtraWidget extends Component {

  val key: WidgetKey

  private var _propertyMap: PropertyMap = Map.empty

  def propertyMap = _propertyMap

  def propertyDefs: Map[PropertyKey, PropertyDef[_ <: ExtraWidget]]

  def update(newPropertyMap: PropertyMap): Unit = {
    val oldPropertyMap = _propertyMap
    _propertyMap = newPropertyMap
    for {
      propertyKey ← oldPropertyMap.keys
      if !newPropertyMap.contains(key)
      prop ← propertyDefs.get(propertyKey)
    } prop.unset

    for {
      (propertyKey, newValue) ← newPropertyMap
      prop ← propertyDefs.get(propertyKey)
    } {
      val oldValue = oldPropertyMap.get(propertyKey)
      if (oldValue != Some(newValue))
        prop.set(newValue, oldValue)
    }
  }
}
