package uk.ac.surrey.soc.cress.extrawidgets

import java.util.Locale.ENGLISH

import scala.collection.immutable

package object api {

  val pluginName = "eXtraWidgets"

  type Key = String
  type WidgetKey = Key
  type PropertyKey = Key
  type PropertyValue = AnyRef

  type PropertyMap = immutable.Map[PropertyKey, PropertyValue]
  type WidgetMap = immutable.Map[WidgetKey, PropertyMap]

  def normalizeKey(key: Key): Key = key.toUpperCase(ENGLISH)

  def makeKey(s: String): Key =
    normalizeKey((" " + s).toCharArray.sliding(2)
      .map { case Array(a, b) ⇒ (if (a.isLower && b.isUpper) "-" else "") + b }
      .mkString).stripPrefix("XW-")

  implicit def toRunnable[T](block: ⇒ T) =
    new Runnable() { def run() { block } }
}
