package uk.ac.surrey.soc.cress.extrawidgets.extension

import org.nlogo.api.Dump
import org.nlogo.api.ExtensionException
import org.nlogo.api.LogoList

import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyMap

package object util {

  def tryTo[A](f: ⇒ Either[String, A]): A =
    f match {
      case Right(a) ⇒ a
      case Left(msg) ⇒ throw new ExtensionException(msg)
    }

  implicit def enrichLogoList(l: LogoList): RichLogoList = new RichLogoList(l)
  class RichLogoList(logoList: LogoList) {
    def toPropertyMap: PropertyMap = {
      logoList.toVector.map { obj ⇒
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
