package uk.ac.surrey.xw.extension

import scala.language.implicitConversions

import org.nlogo.api.Dump
import org.nlogo.api.ExtensionException
import org.nlogo.api.ExtensionManager
import org.nlogo.app.App
import org.nlogo.app.AppFrame
import org.nlogo.core.LogoList
import org.nlogo.nvm.Activation
import org.nlogo.nvm.AnonymousCommand
import org.nlogo.nvm.Context
import org.nlogo.window.GUIWorkspace
import org.nlogo.workspace.AbstractWorkspace

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

  def getWorkspace(extensionManager: ExtensionManager): AbstractWorkspace =
    extensionManager
      .asInstanceOf[org.nlogo.workspace.ExtensionManager]
      .workspace
      .asInstanceOf[AbstractWorkspace]

  def getApp(extensionManager: ExtensionManager): Option[App] =
    Seq(getWorkspace(extensionManager))
      .collect { case ws: GUIWorkspace ⇒ ws }
      .map(_.getFrame)
      .collect { case af: AppFrame ⇒ af }
      .flatMap(_.getLinkChildren)
      .collect { case app: App ⇒ app }
      .headOption

  def runTask(workspace: AbstractWorkspace, context: Context, task: AnonymousCommand, args: Array[AnyRef]): Unit = {
    val childContext = new Context(context, workspace.world.observers)
   context.activation = new Activation(
      task.procedure, childContext.activation, task.locals, 0,
      task.binding.enterScope(task.formals, args))
    childContext.ip = -1 // makeConcurrentJob increments the ip and we want to start at 0
    // Since this has to be run as a top level job, we use ConcurrentJob. BCH 4/22/2015
    workspace.jobManager.addJob(childContext.makeConcurrentJob(workspace.world.observers), waitForCompletion = false)
  }
}
