package uk.ac.surrey.soc.cress.extrawidgets.extension

import org.nlogo.api.Dump
import org.nlogo.api.ExtensionException
import org.nlogo.api.LogoList
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyMap
import uk.ac.surrey.soc.cress.extrawidgets.api.XWException

package object util {

  implicit def enrichEither[L, R](either: Either[L, R]) = new RichEither(either)
  class RichEither[L, R](either: Either[L, R]) {
    def rightOrThrow: R = either match {
      case Right(r) ⇒ r
      case Left(l) ⇒ throw l match {
        case e: Exception ⇒ new ExtensionException(e)
        case s: String ⇒ new ExtensionException(s)
        case x ⇒ new ExtensionException("Unexpected result: " + x.toString)
      }
    }
  }

  implicit def enrichVector[T <: AnyRef](v: Vector[T]): RichVector[T] = new RichVector(v)
  class RichVector[T <: AnyRef](v: Vector[T]) {
    def toPropertyMap: PropertyMap = {
      v.map { obj ⇒
        val list: LogoList = try obj.asInstanceOf[LogoList] catch {
          case e: ClassCastException ⇒ throw new ExtensionException(
            Dump.logoObject(obj) + " is not a list.", e)
        }
        val keyObj: AnyRef = try list.get(0) catch {
          case e: IndexOutOfBoundsException ⇒ throw new ExtensionException(
            Dump.list(list) + " does not contain two elements.", e)
        }
        val key: String = try keyObj.asInstanceOf[String] catch {
          case e: ClassCastException ⇒ throw throw new ExtensionException(
            "Trying to use " + Dump.logoObject(keyObj) + " as a key, but it is not a string.", e)
        }
        val value: AnyRef = try list.get(1) catch {
          case e: IndexOutOfBoundsException ⇒ new ExtensionException(
            Dump.list(list) + " does not contain two elements.", e)
        }
        key -> value
      }(collection.breakOut)
    }
  }
}
