package uk.ac.surrey.soc.cress.extrawidgets.api

import java.lang.reflect.Method

import org.nlogo.window.GUIWorkspace

class WidgetKind(clazz: Class[_ <: ExtraWidget]) {

  val name = makeKey(clazz.getSimpleName)

  val constructor =
    clazz.getConstructor(
      classOf[WidgetKey],
      classOf[StateUpdater],
      classOf[GUIWorkspace])

  def newInstance(widgetKey: WidgetKey, stateUpdater: StateUpdater, ws: GUIWorkspace) =
    constructor.newInstance(widgetKey, stateUpdater, ws)

  val propertyMethods: Seq[Method] =
    clazz.getMethods.filter { method ⇒
      classOf[PropertyDef[_]]
        .isAssignableFrom(method.getReturnType)
    }

  val propertyKeys: Seq[PropertyKey] =
    propertyMethods.map { method ⇒ makePropertyKey(method) }

  def propertyDefs(w: ExtraWidget): Map[PropertyKey, PropertyDef[_]] =
    (propertyKeys zip propertyMethods.map { method ⇒
      method.invoke(w).asInstanceOf[PropertyDef[_]]
    })(collection.breakOut)
}
