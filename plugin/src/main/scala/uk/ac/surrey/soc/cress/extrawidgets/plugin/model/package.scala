package uk.ac.surrey.soc.cress.extrawidgets.plugin

import java.util.concurrent.{ ConcurrentHashMap ⇒ JCMap }

import scala.collection.JavaConverters.asScalaConcurrentMapConverter
import scala.collection.Map
import scala.collection.mutable.{ ConcurrentMap ⇒ CMap }

import org.nlogo.api.ExtensionManager

package object model {

  type Kind = String
  type WidgetName = String
  type PropertyName = String
  type PropertyValue = Any
  type MutableStore = CMap[WidgetName, CMap[PropertyName, PropertyValue]]
  type Store = Map[WidgetName, Map[PropertyName, PropertyValue]]

  def cast[A](x: Any) =
    try Some(x.asInstanceOf[A])
    catch { case e: ClassCastException ⇒ None }

  def getOrCreateStoreIn(extensionManager: ExtensionManager): MutableStore = {
    // TODO: if there is already some object stored in the extensionManager,
    // we should raise an exception and explain the situation to the user...
    def store: Option[MutableStore] =
      Option(extensionManager.retrieveObject).flatMap(cast[MutableStore])
    def create(): MutableStore = {
      val store: MutableStore =
        new JCMap[WidgetName, CMap[PropertyName, PropertyValue]](2).asScala
      extensionManager.storeObject(store)
      store
    }
    store.getOrElse(create())
  }

  def newWidgetMap = new JCMap[PropertyName, PropertyValue]().asScala

}