package uk.ac.surrey.soc.cress.extrawidgets.plugin

import java.util.concurrent.ConcurrentHashMap

import scala.collection.JavaConverters.asScalaConcurrentMapConverter
import scala.collection.Map
import scala.collection.mutable.ConcurrentMap

import org.nlogo.api.ExtensionManager

package object model {

  type WidgetKind = String
  type WidgetID = String
  type PropertyName = String
  type PropertyValue = Any
  type PropertyMap = Map[PropertyName, PropertyValue]
  type MutablePropertyMap = ConcurrentMap[PropertyName, PropertyValue]
  type WidgetMap = Map[WidgetID, PropertyMap]
  type MutableWidgetMap = ConcurrentMap[WidgetID, MutablePropertyMap]

  def getOrCreateWidgetMapIn(extensionManager: ExtensionManager): MutableWidgetMap =
    // TODO: if there is already some object stored in the extensionManager,
    // we should raise catch the cast exception and explain the situation to the user...
    Option(extensionManager.retrieveObject)
      .map(_.asInstanceOf[MutableWidgetMap])
      .getOrElse {
        val widgetMap = newWidgetMap
        extensionManager.storeObject(widgetMap)
        widgetMap
      }

  def newWidgetMap = new ConcurrentHashMap[WidgetID, MutablePropertyMap](2).asScala
  def newPropertyMap = new ConcurrentHashMap[PropertyName, PropertyValue]().asScala

}