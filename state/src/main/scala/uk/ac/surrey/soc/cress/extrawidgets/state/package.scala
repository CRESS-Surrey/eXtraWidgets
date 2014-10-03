package uk.ac.surrey.soc.cress.extrawidgets

import java.util.concurrent.ConcurrentSkipListMap

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


  // Note: we use ConcurrentSkipListMap instead of ConcurrentHashMap
  // to ensure reproducibility of runs across architectures. It's also
  // nice to have ordered keys. NP 2014-10-03.
  private def newWidgetMap: MutableWidgetMap = {
    new ConcurrentSkipListMap[WidgetKey, MutablePropertyMap]().asScala
  }

  def newPropertyMap: MutablePropertyMap = {
    new ConcurrentSkipListMap[PropertyKey, PropertyValue]().asScala
  }

}
