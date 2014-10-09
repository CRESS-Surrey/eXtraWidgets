package uk.ac.surrey.soc.cress.extrawidgets.api

import java.awt.Component

trait ExtraWidget extends Component {

  val key: WidgetKey
  val kind: Kind
  val self: kind.W

  private var _propertyMap: PropertyMap = Map.empty

  def propertyMap = _propertyMap

  def update(newPropertyMap: PropertyMap): Unit = {
    val oldPropertyMap = _propertyMap
    _propertyMap = newPropertyMap
    for {
      propertyKey ← oldPropertyMap.keys
      if !newPropertyMap.contains(key)
      prop ← kind.propertyDefMap.get(propertyKey)
    } prop.unsetValueFor(self)

    for {
      (propertyKey, newValue) ← newPropertyMap
      prop ← kind.propertyDefMap.get(propertyKey)
    } {
      val oldValue = oldPropertyMap.get(propertyKey)
      if (oldValue != Some(newValue))
        prop.setValueFor(self, newValue, oldValue)
    }
  }
}
