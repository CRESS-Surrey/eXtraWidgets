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
import uk.ac.surrey.soc.cress.extrawidgets.api.XWException
import uk.ac.surrey.soc.cress.extrawidgets.state.Reader
import uk.ac.surrey.soc.cress.extrawidgets.state.Writer
import uk.ac.surrey.soc.cress.extrawidgets.api.util.normalizeKey

package object state {

  type MutablePropertyMap = ConcurrentMap[PropertyKey, PropertyValue]
  type MutableWidgetMap = ConcurrentMap[WidgetKey, MutablePropertyMap]

  def getOrCreateModel(extensionManager: ExtensionManager): (Reader, Writer) = {
    // TODO: if there is already some object stored in the extensionManager,
    // we should catch the cast exception and explain the situation to the user...
    val (publisher, widgetMap) =
      Option(extensionManager.retrieveObject)
        .map(_.asInstanceOf[(SimpleChangeEventPublisher, MutableWidgetMap)])
        .getOrElse { (new SimpleChangeEventPublisher, newMutableWidgetMap) }
    extensionManager.storeObject((publisher, widgetMap))
    val reader = new Reader(widgetMap, publisher)
    val writer = new Writer(widgetMap, publisher, reader)
    (reader, writer)
  }

  // Note: we use ConcurrentSkipListMap instead of ConcurrentHashMap
  // to ensure reproducibility of runs across architectures. It's also
  // nice to have ordered keys. NP 2014-10-03.
  private def newMutableWidgetMap: MutableWidgetMap = {
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

  implicit def enrichOption[A](o: Option[A]) = new RichOption(o)
  class RichOption[A](o: Option[A]) {
    def orException(msg: String): Either[XWException, A] =
      o.toRight(new XWException(msg, null))
  }

  def tryTo[A](f: ⇒ A, failureMessage: String = ""): Either[XWException, A] =
    try Right(f) catch {
      case e: Exception ⇒ Left(new XWException(
        Option(failureMessage).filter(_.nonEmpty).getOrElse(e.getMessage), e))
    }
}
