package uk.ac.surrey.soc.cress.extrawidgets

import java.util.Locale.ENGLISH
import scala.collection.immutable
import java.lang.reflect.Method

package object api {

  val pluginName = "eXtraWidgets"

  type Key = String
  type WidgetKey = Key
  type PropertyKey = Key
  type PropertyValue = AnyRef

  type PropertyMap = immutable.Map[PropertyKey, PropertyValue]
  type WidgetMap = immutable.Map[WidgetKey, PropertyMap]

  def normalizeKey(key: Key): Key = key.toUpperCase(ENGLISH)

  def makePropertyKey(method: Method) =
    makeKey(method.getName) +
      (if (classOf[BooleanPropertyDef[_]]
        .isAssignableFrom(method.getReturnType))
        "?" else "")

  def makeKey(s: String): Key =
    normalizeKey((" " + s).toCharArray.sliding(2)
      .map { case Array(a, b) ⇒ (if (a.isLower && b.isUpper) "-" else "") + b }
      .mkString).stripPrefix("XW-")

  implicit def toRunnable[T](block: ⇒ T) =
    new Runnable() { def run() { block } }

  def const[T](v: T): () ⇒ T = () ⇒ v

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
