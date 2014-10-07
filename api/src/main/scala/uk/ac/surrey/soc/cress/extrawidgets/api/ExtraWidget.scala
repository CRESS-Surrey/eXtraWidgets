package uk.ac.surrey.soc.cress.extrawidgets.api

trait ExtraWidget[W <: ExtraWidget[W]] {
  self: W ⇒

  val kind: Kind[W]
  val key: WidgetKey

  private var currentPropertyMap: PropertyMap = Map.empty

  def update(newPropertyMap: PropertyMap): Unit = {

    for {
      propertyKey ← currentPropertyMap.keys
      if !newPropertyMap.contains(key)
      prop ← kind.propertyDefs.get(propertyKey)
    } prop.unset(this)

    for {
      (propertyKey, newValue) ← newPropertyMap
      prop ← kind.propertyDefs.get(propertyKey)
    } {
      val oldValue = currentPropertyMap.get(propertyKey)
      if (oldValue != Some(newValue))
        prop.set(this, newValue, oldValue)
    }
  }

}
