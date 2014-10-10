package uk.ac.surrey.soc.cress.extrawidgets.api

import java.awt.Component

trait ExtraWidget extends Component {

  val key: WidgetKey

  private var _propertyMap: PropertyMap = Map.empty

  final def propertyMap = _propertyMap

  /* Let's be careful not to access the following lazy vals
   * in this trait's constructor because the property fields
   * in `this` won't be initialised yet. NP 2014-10-10.
   */
  lazy val kind = new WidgetKind(this.getClass)
  lazy val propertyDefs = kind.propertyDefs(this)

  def update(newPropertyMap: PropertyMap): Unit = {
    val oldPropertyMap = _propertyMap
    _propertyMap = newPropertyMap
    for {
      propertyKey ← oldPropertyMap.keys
      if !newPropertyMap.contains(key)
      prop ← propertyDefs.get(propertyKey)
    } prop.unsetValue

    for {
      (propertyKey, newValueObj) ← newPropertyMap
      prop ← propertyDefs.get(propertyKey)
    } {
      val oldValue = oldPropertyMap.get(propertyKey).map(_.asInstanceOf[prop.ValueType])
      val newValue = newValueObj.asInstanceOf[prop.ValueType]
      if (oldValue != Some(newValue))
        prop.setValue(newValue, oldValue)
    }
  }
}
