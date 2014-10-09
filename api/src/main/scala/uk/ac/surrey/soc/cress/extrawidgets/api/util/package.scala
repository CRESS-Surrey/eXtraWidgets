package uk.ac.surrey.soc.cress.extrawidgets.api

import java.util.Locale.ENGLISH

package object util {

  def normalizeKey(key: String) = key.toUpperCase(ENGLISH)

  implicit def toRunnable[T](block: ⇒ T) =
    new Runnable() { def run() { block } }
}
