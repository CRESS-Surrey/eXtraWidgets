package uk.ac.surrey.soc.cress.extrawidgets.gui

import org.nlogo.window.GUIWorkspace

import uk.ac.surrey.soc.cress.extrawidgets.api.ExtraWidget
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyMap
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKey
import uk.ac.surrey.soc.cress.extrawidgets.api.util.makeKey

class WidgetKind(clazz: Class[_ <: ExtraWidget]) {

  val name = makeKey(clazz.getSimpleName)

  val constructor = clazz.getConstructor(classOf[WidgetKey], classOf[PropertyMap], classOf[GUIWorkspace])
  def newInstance(widgetKey: WidgetKey, propertyMap: PropertyMap, ws: GUIWorkspace) =
    constructor.newInstance(widgetKey, propertyMap, ws)
}
