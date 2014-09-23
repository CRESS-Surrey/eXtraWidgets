package uk.ac.surrey.soc.cress.extrawidgets.plugin

package object util {
  // see: https://issues.scala-lang.org/browse/SI-5793
  implicit def eitherToRightBiased[A, B](e: Either[A, B]) =
    new RightBiasedEither(e)
  class RightBiasedEither[A, B](val e: Either[A, B]) {
    def foreach[U](f: B ⇒ U): Unit = e.right.foreach(f)
    def map[C](f: B ⇒ C): Either[A, C] = e.right.map(f)
    def flatMap[C](f: B ⇒ Either[A, C]) = e.right.flatMap(f)
  }
}
