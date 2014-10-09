package uk.ac.surrey.soc.cress.extrawidgets.api

import java.awt.Component

import uk.ac.surrey.soc.cress.extrawidgets.api.util.makeKey

trait ExtraWidget extends Component {

  val key: WidgetKey

  private var _propertyMap: PropertyMap = Map.empty

  def propertyMap = _propertyMap

  lazy val propertyDefs: Map[PropertyKey, PropertyDef[ExtraWidget]] =
    this.getClass.getFields
      .filter { field ⇒ classOf[PropertyDef[ExtraWidget]].isAssignableFrom(field.getType) }
      .map { field ⇒
        val propertyKey = makeKey(field.getType.getSimpleName)
        val propertyDef = field.get(this).asInstanceOf[PropertyDef[ExtraWidget]]
        propertyKey -> propertyDef
      }(collection.breakOut)

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
