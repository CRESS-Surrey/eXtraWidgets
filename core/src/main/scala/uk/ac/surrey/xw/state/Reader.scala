package uk.ac.surrey.xw.state

import scala.collection.JavaConverters.asJavaConcurrentMapConverter
import scala.collection.JavaConverters.mapAsJavaMapConverter

import org.json.simple.JSONObject

import Strings.propertyMustBeNonEmpty
import Strings.propertyMustBeUnique
import uk.ac.surrey.xw.api.PropertyKey
import uk.ac.surrey.xw.api.PropertyMap
import uk.ac.surrey.xw.api.PropertyValue
import uk.ac.surrey.xw.api.WidgetKey
import uk.ac.surrey.xw.api.XWException
import uk.ac.surrey.xw.api.enrichEither
import uk.ac.surrey.xw.api.enrichOption
import uk.ac.surrey.xw.api.normalizeString

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

  def get(propertyKey: PropertyKey, widgetKey: WidgetKey): PropertyValue =
    mutablePropertyMap(widgetKey)
      .rightOrThrow
      .get(normalizeString(propertyKey))
      .getOrElse(throw XWException(
        "Property " + propertyKey + " does not exist for widget " + widgetKey + "."))

  def widgetKeySet: Set[WidgetKey] = widgetMap.keys.toSet

  def widgetKeyVector: Vector[WidgetKey] = Vector() ++ widgetMap.keys

  private def mutablePropertyMap(widgetKey: WidgetKey): Either[XWException, MutablePropertyMap] =
    widgetMap.get(normalizeString(widgetKey)).orException(
      "Widget " + widgetKey + " does not exist.")

  def propertyMap(widgetKey: WidgetKey): Either[XWException, PropertyMap] =
    mutablePropertyMap(widgetKey).right.map(_.toMap)

  def contains(widgetKey: WidgetKey) = widgetMap.contains(normalizeString(widgetKey))

  def propertyKeyVector(widgetKey: WidgetKey): Either[XWException, Vector[PropertyKey]] =
    mutablePropertyMap(widgetKey).right.map(Vector() ++ _.keysIterator)

  def properties(widgetKey: WidgetKey): Either[XWException, Vector[(PropertyKey, PropertyValue)]] =
    mutablePropertyMap(widgetKey).right.map(Vector() ++ _.iterator)

  def toJSON = new JSONObject(widgetMap.mapValues(_.asJava).asJava).toJSONString
}
