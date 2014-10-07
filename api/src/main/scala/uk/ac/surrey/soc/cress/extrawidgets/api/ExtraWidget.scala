package uk.ac.surrey.soc.cress.extrawidgets.api

trait ExtraWidget {

  val key: WidgetKey

  private var currentPropertyMap: PropertyMap = Map.empty

  def propertyDefs: Map[PropertyKey, PropertyDef[_ <: ExtraWidget]]

  def update(newPropertyMap: PropertyMap): Unit = {

    for {
      propertyKey ← currentPropertyMap.keys
      if !newPropertyMap.contains(key)
      prop ← propertyDefs.get(propertyKey)
    } prop.unset

    for {
      (propertyKey, newValue) ← newPropertyMap
      prop ← propertyDefs.get(propertyKey)
    } {
      val oldValue = currentPropertyMap.get(propertyKey)
      if (oldValue != Some(newValue))
        prop.set(newValue, oldValue)
    }
  }
}
