package uk.ac.surrey.xw

import java.util.concurrent.ConcurrentSkipListMap

import scala.collection.JavaConverters.mapAsScalaConcurrentMapConverter
import scala.collection.concurrent
import scala.language.implicitConversions

import uk.ac.surrey.xw.api.PropertyKey
import uk.ac.surrey.xw.api.PropertyMap
import uk.ac.surrey.xw.api.PropertyValue
import uk.ac.surrey.xw.api.WidgetKey
import uk.ac.surrey.xw.api.normalizeString

package object state {

  type MutablePropertyMap = concurrent.Map[PropertyKey, PropertyValue]
  type MutableWidgetMap = concurrent.Map[WidgetKey, MutablePropertyMap]

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
    def normalizeKeys = m.map { case (k, v) ⇒ normalizeString(k) -> v }
    def asMutablePropertyMap: MutablePropertyMap = {
      val mm = new ConcurrentSkipListMap[PropertyKey, PropertyValue].asScala
      for ((k, v) ← m) mm += k -> v
      mm
    }
  }
}
