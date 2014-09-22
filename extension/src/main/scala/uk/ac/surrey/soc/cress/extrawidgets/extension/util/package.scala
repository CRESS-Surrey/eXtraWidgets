package uk.ac.surrey.soc.cress.extrawidgets.extension

import org.nlogo.api.ExtensionException

package object util {

  def tryTo[A](f: ⇒ Either[String, A]): A =
    f match {
      case Right(a) ⇒ a
      case Left(msg) ⇒ throw new ExtensionException(msg)
    }

}
