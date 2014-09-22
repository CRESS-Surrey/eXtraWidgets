package uk.ac.surrey.soc.cress.extrawidgets.plugin.data

import scala.collection.mutable.{ Map ⇒ MMap }
import uk.ac.surrey.soc.cress.extrawidgets.plugin.GUIStrings.Data.TabNameMustBeNonEmpty
import uk.ac.surrey.soc.cress.extrawidgets.plugin.GUIStrings.Data._
import org.nlogo.api.ExtensionManager

object ExtraWidgetsData {
  def getOrCreateIn(extensionManager: ExtensionManager): MutableExtraWidgetsData = {
    def store: Option[Store] =
      Option(extensionManager.retrieveObject).flatMap { obj ⇒
        try { Some(obj.asInstanceOf[Store]) }
        catch { case e: ClassCastException ⇒ None }
      }
    def create(): Store = {
      val store = MMap[WidgetName, MMap[PropertyName, PropertyValue]]()
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

  def validateNonEmptyName(name: String) =
    Either.cond(name.nonEmpty, name, TabNameMustBeNonEmpty)

  def validateUniqueName(kind: String, name: String) = {
    val otherNames = for {
      (n, w) ← store
      k ← w.get("kind")
      if k == kind
    } yield n
    Either.cond(isUnique(name, otherNames), name, nameMustBeUnique(kind, name))
  }

  def isUnique(name: String, existingNames: Iterable[String]) =
    !existingNames.exists(_ == name)

}

class MutableExtraWidgetsData(store: Store) extends ExtraWidgetsData(store) {

  def add(kind: String, name: String): Either[String, String] =
    for {
      _ ← validateNonEmptyName(name).right
      _ ← validateUniqueName(kind, name).right
    } yield {
      val w = MMap[PropertyName, PropertyValue]()
      w += "kind" -> kind
      store += name -> w
      println(store)
      name
    }

}