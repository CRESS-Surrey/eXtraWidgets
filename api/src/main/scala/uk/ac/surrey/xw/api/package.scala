package uk.ac.surrey.xw

import java.util.Locale.ENGLISH

import scala.Left
import scala.Right
import scala.collection.immutable
import scala.language.implicitConversions

import org.nlogo.core.LogoList

import uk.ac.surrey.xw.api.XWException

package object api {

  type Key = String
  type WidgetKey = Key
  type PropertyKey = Key
  type PropertyValue = AnyRef
  type KindName = String

  type PropertyMap = immutable.Map[PropertyKey, PropertyValue]
  type WidgetMap = immutable.Map[WidgetKey, PropertyMap]

  def normalizeString(s: String): String = s.toUpperCase(ENGLISH)

  def makeKey(s: String): Key =
    normalizeString((" " + s).toCharArray.sliding(2)
      .map { case Array(a, b) ⇒ (if (a.isLower && b.isUpper) "-" else "") + b }
      .mkString)

  implicit def toRunnable[T](block: ⇒ T) =
    new Runnable() { def run() { block } }

  def const[T](v: T): () ⇒ T = () ⇒ v

  implicit def enrichOption[A](o: Option[A]) = new RichOption(o)
  class RichOption[A](o: Option[A]) {
    def orException(msg: String): Either[XWException, A] =
      o.toRight(new XWException(msg, null))
  }

  implicit def enrichEither[L, R](either: Either[L, R]) = new RichEither(either)
  class RichEither[L, R](either: Either[L, R]) {
    def rightOrThrow: R = either match {
      case Right(r) ⇒ r
      case Left(l) ⇒ throw l match {
        case e: XWException ⇒ e
        case e: Exception ⇒ XWException(e.getMessage, e)
        case s: String ⇒ XWException(s)
        case x ⇒ XWException("Unexpected result: " + x.toString)
      }
    }
  }

  def tryTo[A](f: ⇒ A, failureMessage: String = null): Either[XWException, A] =
    try Right(f) catch {
      case e: XWException ⇒ Left(e)
      case e: Exception ⇒ Left(XWException(
        Option(failureMessage).getOrElse(e.getMessage), e))
    }

  def colorToLogoList(c: java.awt.Color): LogoList = {
    val a =
      if (c.getAlpha != 0) c.getRGBComponents(null)
      else c.getRGBColorComponents(null)
    LogoList.fromIterator(a.map(Float.box).iterator)
  }

}
