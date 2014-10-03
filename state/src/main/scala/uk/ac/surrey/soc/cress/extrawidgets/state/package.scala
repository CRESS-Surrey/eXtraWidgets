package uk.ac.surrey.soc.cress.extrawidgets

import java.util.concurrent.ConcurrentHashMap

import scala.collection.JavaConverters.asScalaConcurrentMapConverter
import scala.collection.mutable.ConcurrentMap

import org.nlogo.api.ExtensionManager
import org.nlogo.api.SimpleChangeEventPublisher

import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyKey
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyValue
import uk.ac.surrey.soc.cress.extrawidgets.api._
import uk.ac.surrey.soc.cress.extrawidgets.state.Reader
import uk.ac.surrey.soc.cress.extrawidgets.state.Writer

package object state {

  type MutablePropertyMap = ConcurrentMap[PropertyKey, PropertyValue]
  type MutableWidgetMap = ConcurrentMap[WidgetKey, MutablePropertyMap]

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
    new ConcurrentHashMap[WidgetKey, MutablePropertyMap](
      initialCapacity, loadFactor, concurrencyLevel
    ).asScala
  }

  def newPropertyMap: MutablePropertyMap = {
    new ConcurrentHashMap[PropertyKey, PropertyValue](
      initialCapacity, loadFactor, concurrencyLevel
    ).asScala
  }

  implicit def enrichMutableWidgetMap(widgetMap: MutableWidgetMap) =
    new RichMutableWidgetMap(widgetMap)

  class RichMutableWidgetMap(widgetMap: MutableWidgetMap) {
    def toImmutable: WidgetMap = Map(widgetMap.mapValues(_.toImmutable).toSeq: _*)
  }

  implicit def enrichMutablePropertyMap(propertyMap: MutablePropertyMap) =
    new RichMutablePropertyMap(propertyMap)

  class RichMutablePropertyMap(propertyMap: MutablePropertyMap) {
    def toImmutable: PropertyMap = Map(propertyMap.toSeq: _*)
  }
}
