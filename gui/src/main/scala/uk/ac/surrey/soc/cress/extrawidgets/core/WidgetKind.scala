package uk.ac.surrey.soc.cress.extrawidgets.core

import java.util.Locale.ENGLISH

import org.nlogo.window.GUIWorkspace

import uk.ac.surrey.soc.cress.extrawidgets.api.ExtraWidget
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyMap
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKey

class WidgetKind(clazz: Class[_ <: ExtraWidget]) {

  val name =
    (" " + clazz.getSimpleName).toCharArray.sliding(2)
      .map { case Array(a, b) ⇒ if (a.isLower && b.isUpper) "-" + b else b.toString }
      .mkString.toUpperCase(ENGLISH)

  val constructor = clazz.getConstructor(classOf[WidgetKey], classOf[PropertyMap], classOf[GUIWorkspace])
  def newInstance(widgetKey: WidgetKey, propertyMap: PropertyMap, ws: GUIWorkspace) =
    constructor.newInstance(widgetKey, propertyMap, ws)
}
