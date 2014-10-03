package uk.ac.surrey.soc.cress.extrawidgets.api

trait ExtraWidget {
  val key: WidgetKey
  private var propertyMap: PropertyMap = Map.empty
}
