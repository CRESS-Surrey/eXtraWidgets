package uk.ac.surrey.soc.cress.extrawidgets.api

import java.util.Locale.ENGLISH

package object util {

  def normalizeKey(key: Key): Key = key.toUpperCase(ENGLISH)

  def makeKey(s: String): Key =
    normalizeKey((" " + s).toCharArray.sliding(2)
      .map { case Array(a, b) ⇒ if (a.isLower && b.isUpper) "-" + b else b.toString }
      .mkString)

  implicit def toRunnable[T](block: ⇒ T) =
    new Runnable() { def run() { block } }
}
