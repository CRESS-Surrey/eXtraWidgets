package uk.ac.surrey.soc.cress.extrawidgets

import uk.ac.surrey.soc.cress.extrawidgets.api.XWException

package object util {

  implicit def enrichOption[A](o: Option[A]) = new RichOption(o)
  class RichOption[A](o: Option[A]) {
    def orException(msg: String): Either[XWException, A] =
      o.toRight(new XWException(msg, null))
  }

  def tryTo[A](f: ⇒ A, failureMessage: String = ""): Either[XWException, A] =
    try Right(f) catch {
      case e: Exception ⇒ Left(new XWException(
        Option(failureMessage).filter(_.nonEmpty).getOrElse(e.getMessage), e))
    }

}
