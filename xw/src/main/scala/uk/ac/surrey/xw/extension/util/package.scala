package uk.ac.surrey.xw.extension

import org.nlogo.api.Dump
import org.nlogo.api.ExtensionException
import org.nlogo.api.ExtensionManager
import org.nlogo.api.LogoList
import org.nlogo.app.App
import org.nlogo.app.AppFrame
import org.nlogo.window.GUIWorkspace

import uk.ac.surrey.xw.api.PropertyMap

package object util {

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

  def getApp(extensionManager: ExtensionManager): Option[App] =
    Seq(extensionManager)
      .collect { case em: org.nlogo.workspace.ExtensionManager ⇒ em }
      .map(_.workspace)
      .collect { case ws: GUIWorkspace ⇒ ws }
      .map(_.getFrame)
      .collect { case af: AppFrame ⇒ af }
      .flatMap(_.getLinkChildren)
      .collect { case app: App ⇒ app }
      .headOption
}
