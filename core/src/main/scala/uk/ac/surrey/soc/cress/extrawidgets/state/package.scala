package uk.ac.surrey.soc.cress.extrawidgets

import java.util.concurrent.ConcurrentSkipListMap

import scala.collection.JavaConverters.asScalaConcurrentMapConverter
import scala.collection.mutable.ConcurrentMap

import org.nlogo.api.ExtensionManager
import org.nlogo.api.SimpleChangeEventPublisher

import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyKey
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyMap
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyValue
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKey
import uk.ac.surrey.soc.cress.extrawidgets.api.normalizeKey
import uk.ac.surrey.soc.cress.extrawidgets.state.Reader
import uk.ac.surrey.soc.cress.extrawidgets.state.Writer

package object state {

  type MutablePropertyMap = ConcurrentMap[PropertyKey, PropertyValue]
  type MutableWidgetMap = ConcurrentMap[WidgetKey, MutablePropertyMap]

  // Note: we use ConcurrentSkipListMap instead of ConcurrentHashMap
  // to ensure reproducibility of runs across architectures. It's also
  // nice to have ordered keys. NP 2014-10-03.
  def newMutableWidgetMap: MutableWidgetMap = {
    new ConcurrentSkipListMap[WidgetKey, MutablePropertyMap]().asScala
  }

  def newMutablePropertyMap: MutablePropertyMap = {
    new ConcurrentSkipListMap[PropertyKey, PropertyValue]().asScala
  }

  implicit def enrichPropertyMap(m: PropertyMap) = new RichPropertyMap(m)
  class RichPropertyMap(m: PropertyMap) {
    def asMutablePropertyMap: MutablePropertyMap = {
      val mm = new ConcurrentSkipListMap[PropertyKey, PropertyValue].asScala
      for ((k, v) ← m) mm += normalizeKey(k) -> v
      mm
    }
  }
}
