package uk.ac.surrey.soc.cress.extrawidgets.api

import scala.Array.fallbackCanBuildFrom
import org.nlogo.window.GUIWorkspace
import java.lang.reflect.Field
import java.lang.reflect.Method

class WidgetKind(clazz: Class[_ <: ExtraWidget]) {

  val name = makeKey(clazz.getSimpleName)

  val constructor = clazz.getConstructor(classOf[WidgetKey], classOf[PropertyMap], classOf[GUIWorkspace])
  def newInstance(widgetKey: WidgetKey, propertyMap: PropertyMap, ws: GUIWorkspace) =
    constructor.newInstance(widgetKey, propertyMap, ws)

  val propertyMethods: Seq[Method] =
    clazz.getMethods.filter { method ⇒
      classOf[PropertyDef[_ <: ExtraWidget]].isAssignableFrom(method.getReturnType)
    }

  val propertyKeys: Seq[PropertyKey] =
    propertyMethods.map { method ⇒ makeKey(method.getName) }

  def propertyDefs(w: ExtraWidget): Map[PropertyKey, PropertyDef[_ <: ExtraWidget]] =
    (propertyKeys zip propertyMethods.map { method ⇒
      method.invoke(w).asInstanceOf[PropertyDef[_ <: ExtraWidget]]
    })(collection.breakOut)
}
