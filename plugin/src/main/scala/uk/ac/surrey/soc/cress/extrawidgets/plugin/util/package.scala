package uk.ac.surrey.soc.cress.extrawidgets.plugin

package object util {

  implicit def toRunnable[T](block: ⇒ T) =
    new Runnable() { def run() { block } }

}
