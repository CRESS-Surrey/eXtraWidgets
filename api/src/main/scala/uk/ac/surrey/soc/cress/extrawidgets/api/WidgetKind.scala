package uk.ac.surrey.soc.cress.extrawidgets.api

import java.lang.reflect.Method
import org.nlogo.window.GUIWorkspace
import uk.ac.surrey.soc.cress.extrawidgets.api.annotations._

class WidgetKind(clazz: Class[_ <: ExtraWidget]) {

  val name = makeKey(clazz.getSimpleName)

  val constructor =
    clazz.getConstructor(
      classOf[WidgetKey],
      classOf[StateUpdater],
      classOf[GUIWorkspace])

  def newInstance(widgetKey: WidgetKey, stateUpdater: StateUpdater, ws: GUIWorkspace) =
    constructor.newInstance(widgetKey, stateUpdater, ws)

  private def isProperty(method: Method) =
    classOf[Property[_]].isAssignableFrom(method.getReturnType)

  private val methods: Map[PropertyKey, Method] =
    clazz.getMethods.collect {
      case method if isProperty(method) ⇒
        makePropertyKey(method) -> method
    }(collection.breakOut)

  val defaultProperty: Option[String] =
    Option(clazz.getAnnotation(classOf[DefaultProperty])).map(_.value)

  val syntaxes: Map[PropertyKey, PropertySyntax] =
    methods.mapValues { method ⇒
      method.getReturnType // should be some sort of Property
        .getConstructors.head // with one and only one constructor
        .newInstance(null, null) // that we use to instantiate a dummy Property
        .asInstanceOf[PropertySyntax] // that implements the PropertySyntax trait
    }

  def properties(w: ExtraWidget): Map[PropertyKey, Property[_]] =
    methods.mapValues { method ⇒
      method.invoke(w).asInstanceOf[Property[_]]
    }
}
