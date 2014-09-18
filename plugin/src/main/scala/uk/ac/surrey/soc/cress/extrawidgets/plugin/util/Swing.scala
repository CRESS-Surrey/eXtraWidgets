package uk.ac.surrey.soc.cress.extrawidgets.plugin.util

object Swing {
  implicit def functionToRunnable[T](f: => T) =
    new Runnable() { def run() { f } }
}