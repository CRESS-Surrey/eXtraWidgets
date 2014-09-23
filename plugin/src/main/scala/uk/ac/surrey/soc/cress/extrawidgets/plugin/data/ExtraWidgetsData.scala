package uk.ac.surrey.soc.cress.extrawidgets.plugin.data

import java.util.concurrent.{ ConcurrentHashMap ⇒ JCMap }

import scala.collection.JavaConverters.asScalaConcurrentMapConverter
import scala.collection.mutable.{ ConcurrentMap ⇒ ScalaCMap }

import org.nlogo.api.ExtensionManager

import uk.ac.surrey.soc.cress.extrawidgets.plugin.GUIStrings.Data.propertyMustBeNonEmpty
import uk.ac.surrey.soc.cress.extrawidgets.plugin.GUIStrings.Data.propertyMustBeUnique
import uk.ac.surrey.soc.cress.extrawidgets.plugin.util.eitherToRightBiased

object ExtraWidgetsData {
  def getOrCreateIn(extensionManager: ExtensionManager): MutableExtraWidgetsData = {
    def store: Option[Store] =
      Option(extensionManager.retrieveObject).flatMap { obj ⇒
        try { Some(obj.asInstanceOf[Store]) }
        catch { case e: ClassCastException ⇒ None }
      }
    def create(): Store = {
      val store: Store =
        new JCMap[WidgetName, ScalaCMap[PropertyName, PropertyValue]](2).asScala
      extensionManager.storeObject(store)
      store
    }
    new MutableExtraWidgetsData(store.getOrElse(create()))
  }
}

class ExtraWidgetsData(store: Store) {

  def cast[A](x: Any) =
    try Some(x.asInstanceOf[A])
    catch { case e: ClassCastException ⇒ None }

  def validateNonEmpty(property: PropertyName, value: String) =
    Either.cond(value.nonEmpty, value, propertyMustBeNonEmpty(property))

  def validateUnique(
    property: PropertyName,
    value: PropertyValue,
    filter: collection.Map[PropertyName, PropertyValue] ⇒ Boolean = _ ⇒ true) = {
    val otherValues = for {
      (v, w) ← store
      if filter(w)
    } yield v
    Either.cond(isUnique(value, otherValues), value, propertyMustBeUnique(property, value))
  }

  def isUnique[A](value: A, existingValues: Iterable[A]) =
    !existingValues.exists(_ == value)

}

class MutableExtraWidgetsData(store: Store) extends ExtraWidgetsData(store) {

  def add(kind: Kind, id: String): Either[String, String] =
    for {
      _ ← validateNonEmpty("id", id)
      _ ← validateUnique("id", id)
    } yield {
      val w = new JCMap[PropertyName, PropertyValue]().asScala
      w += "kind" -> kind
      store += id -> w
      println(store)
      id
    }
}