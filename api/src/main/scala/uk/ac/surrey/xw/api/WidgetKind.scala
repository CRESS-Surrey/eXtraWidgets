package uk.ac.surrey.xw.api

import java.lang.reflect.Method

import org.nlogo.window.GUIWorkspace

import uk.ac.surrey.xw.api.annotations.DefaultProperty
import uk.ac.surrey.xw.api.annotations.PluralName

class WidgetKind(clazz: Class[_ <: ExtraWidget]) {

  val name = makeKey(clazz.getSimpleName)

  val pluralName = {
    Option(clazz.getAnnotation(classOf[PluralName]))
      .map(annotation ⇒ makeKey(annotation.value))
      .getOrElse(name + "S")
  }

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

  val defaultProperty: Option[String] = {
    Option(clazz.getAnnotation(classOf[DefaultProperty]))
      .map { annotation ⇒
        val key = makeKey(annotation.value)
        if (!methods.isDefinedAt(key)) throw XWException(
          key + " is defined as the default property for widget kind " +
            name + " but not such property exists.")
        key
      }
  }

  val metaData: Map[PropertyKey, PropertyMetaData[_]] =
    methods.mapValues { method ⇒
      method.getReturnType // should be some sort of Property
        .getConstructors.head // with one and only one constructor
        .newInstance(null, null) // that we use to instantiate a dummy Property
        .asInstanceOf[PropertyMetaData[_]] // that implements the PropertyMetaData trait
    }

  def properties(w: ExtraWidget): Map[PropertyKey, Property[_]] =
    methods.mapValues { method ⇒
      method.invoke(w).asInstanceOf[Property[_]]
    }
}
