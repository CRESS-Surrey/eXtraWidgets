package uk.ac.surrey.soc.cress.extrawidgets.api

package object util {

  implicit def toRunnable[T](block: ⇒ T) =
    new Runnable() { def run() { block } }

}