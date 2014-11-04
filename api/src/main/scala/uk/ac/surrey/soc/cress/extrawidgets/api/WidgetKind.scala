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
      classOf[Property[_]]
        .isAssignableFrom(method.getReturnType)
    }

  val propertyKeys: Seq[PropertyKey] =
    propertyMethods.map { method ⇒ makePropertyKey(method) }

  def properties(w: ExtraWidget): Map[PropertyKey, Property[_]] =
    (propertyKeys zip propertyMethods.map { method ⇒
      method.invoke(w).asInstanceOf[Property[_]]
    })(collection.breakOut)
}
