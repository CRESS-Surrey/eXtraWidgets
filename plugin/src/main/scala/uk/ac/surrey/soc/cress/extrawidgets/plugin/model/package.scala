package uk.ac.surrey.soc.cress.extrawidgets.plugin

import java.util.concurrent.ConcurrentHashMap

import scala.collection.JavaConverters.asScalaConcurrentMapConverter
import scala.collection.Map
import scala.collection.mutable.ConcurrentMap

import org.nlogo.api.ExtensionManager
import org.nlogo.api.SimpleChangeEventPublisher

import uk.ac.surrey.soc.cress.extrawidgets.plugin.model.Reader
import uk.ac.surrey.soc.cress.extrawidgets.plugin.model.Writer

package object model {

  type WidgetKind = String
  type WidgetID = String
  type PropertyName = String
  type PropertyValue = Any
  type PropertyMap = Map[PropertyName, PropertyValue] //with ObservableMap[PropertyName, PropertyValue]
  type MutablePropertyMap = ConcurrentMap[PropertyName, PropertyValue] //with ObservableMap[PropertyName, PropertyValue]
  type WidgetMap = Map[WidgetID, PropertyMap] //with ObservableMap[WidgetID, PropertyMap]
  type MutableWidgetMap = ConcurrentMap[WidgetID, MutablePropertyMap] //with ObservableMap[WidgetID, PropertyMap]

  def getOrCreateModel(extensionManager: ExtensionManager): (Reader, Writer) = {
    // TODO: if there is already some object stored in the extensionManager,
    // we should catch the cast exception and explain the situation to the user...
    val (publisher, widgetMap) =
      Option(extensionManager.retrieveObject)
        .map(_.asInstanceOf[(SimpleChangeEventPublisher, MutableWidgetMap)])
        .getOrElse { (new SimpleChangeEventPublisher, newWidgetMap) }
    extensionManager.storeObject((publisher, widgetMap))
    val reader = new Reader(widgetMap, publisher)
    val writer = new Writer(widgetMap, publisher, reader)
    (reader, writer)
  }

  /*  Parameters for the ConcurrentHashMaps. concurrencyLevel is 2 as only
   *  the job thread and the AWT event should ever write to the map.
   *  initialCapacity and loadFactor are set at the regular Java defaults.
   *  NP 2014-09-25 */
  private val initialCapacity = 16
  private val loadFactor = 0.75f
  private val concurrencyLevel = 2

  private def newWidgetMap: MutableWidgetMap = {
    new ConcurrentHashMap[WidgetID, MutablePropertyMap](
      initialCapacity, loadFactor, concurrencyLevel
    ).asScala
  }

  def newPropertyMap: MutablePropertyMap = {
    new ConcurrentHashMap[PropertyName, PropertyValue](
      initialCapacity, loadFactor, concurrencyLevel
    ).asScala
  }
}