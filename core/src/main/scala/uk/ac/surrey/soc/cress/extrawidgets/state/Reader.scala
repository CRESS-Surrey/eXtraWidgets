package uk.ac.surrey.soc.cress.extrawidgets.state

import org.nlogo.api.SimpleChangeEvent
import org.nlogo.api.SimpleChangeEventPublisher

import Strings.propertyMustBeNonEmpty
import Strings.propertyMustBeUnique
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyKey
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyMap
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyValue
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKey
import uk.ac.surrey.soc.cress.extrawidgets.api.XWException
import uk.ac.surrey.soc.cress.extrawidgets.api.enrichOption
import uk.ac.surrey.soc.cress.extrawidgets.api.normalizeString

class Reader(
  widgetMap: MutableWidgetMap) { // reader should never expose any part of this

  def validateNonEmpty(propertyKey: PropertyKey, value: String) =
    Option(value).filter(_.nonEmpty)
      .orException(propertyMustBeNonEmpty(propertyKey))

  def validateUnique(
    propertyKey: PropertyKey,
    value: PropertyValue,
    filter: collection.Map[PropertyKey, PropertyValue] ⇒ Boolean = _ ⇒ true) = {
    val otherValues = for {
      (v, w) ← widgetMap
      if filter(w)
    } yield v
    Option(value).filter(isUnique(_, otherValues))
      .orException(propertyMustBeUnique(propertyKey, value))
  }

  def isUnique[A](value: A, existingValues: Iterable[A]) =
    !existingValues.exists(_ == value)

  def get(propertyKey: PropertyKey, widgetKey: WidgetKey): Either[XWException, PropertyValue] =
    for {
      propertyMap ← mutablePropertyMap(widgetKey).right
      propertyValue ← propertyMap.get(normalizeString(propertyKey)).orException(
        "Property " + propertyKey + " does not exist for widget " + widgetKey + ".").right
    } yield propertyValue

  def widgetKeySet: Set[WidgetKey] = widgetMap.keys.toSet

  def widgetKeyVector: Vector[WidgetKey] = Vector() ++ widgetMap.keys

  private def mutablePropertyMap(widgetKey: WidgetKey): Either[XWException, MutablePropertyMap] =
    widgetMap.get(normalizeString(widgetKey)).orException(
      "Widget " + widgetKey + " does not exist in " + widgetMap)

  def propertyMap(widgetKey: WidgetKey): Either[XWException, PropertyMap] =
    mutablePropertyMap(widgetKey).right.map(_.toMap)

  def contains(widgetKey: WidgetKey) = widgetMap.contains(normalizeString(widgetKey))

  def propertyKeyVector(widgetKey: WidgetKey): Either[XWException, Vector[PropertyKey]] =
    mutablePropertyMap(widgetKey).right.map(Vector() ++ _.keysIterator)

  def properties(widgetKey: WidgetKey): Either[XWException, Vector[(PropertyKey, PropertyValue)]] =
    mutablePropertyMap(widgetKey).right.map(Vector() ++ _.iterator)
}
