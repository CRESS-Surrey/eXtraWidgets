package uk.ac.surrey.xw.extension.prim

import scala.collection.mutable.Publisher
import scala.collection.mutable.Subscriber
import scala.collection.parallel.mutable.ParMap

import org.nlogo.api.Argument
import org.nlogo.api.Command
import org.nlogo.api.Context
import org.nlogo.core.Syntax.CommandType
import org.nlogo.core.Syntax.StringType
import org.nlogo.core.Syntax.commandSyntax
import org.nlogo.nvm
import org.nlogo.nvm.Activation
import org.nlogo.nvm.ExtensionContext
import org.nlogo.workspace.AbstractWorkspace

import uk.ac.surrey.xw.api.PropertyKey
import uk.ac.surrey.xw.api.WidgetKey
import uk.ac.surrey.xw.extension.KindInfo
import uk.ac.surrey.xw.extension.WidgetContextManager
import uk.ac.surrey.xw.extension.util.runTask
import uk.ac.surrey.xw.state.RemoveWidget
import uk.ac.surrey.xw.state.{ SetProperty => SetPropEvent }
import uk.ac.surrey.xw.state.StateEvent
import uk.ac.surrey.xw.state.Writer

case class ChangeListener(func: StateEvent => Unit)  extends Subscriber[StateEvent, Publisher[StateEvent]] {
  def notify(pub: Publisher[StateEvent], event: StateEvent): Unit = func(event)
}

object OnChange {
  val listeners = ParMap.empty[(WidgetKey, PropertyKey), ChangeListener]
  def removeListeners(writer: Writer, wk: WidgetKey, pk: PropertyKey) =
    listeners.get((wk, pk)).foreach(writer.removeSubscription)
}

abstract class OnChangePrim(writer: Writer, wcm: WidgetContextManager) extends Command {

  def addListener(context: Context, widgetKey: WidgetKey, propertyKey: PropertyKey, task: nvm.AnonymousCommand): Unit = {
    val extContext = context.asInstanceOf[ExtensionContext]
    val ws = extContext.workspace.asInstanceOf[AbstractWorkspace]
    OnChange.removeListeners(writer, widgetKey, propertyKey)

    val proc = ws.compileForRun("[ [w f v] -> if member? w xw:widgets [ xw:ask w [ (run f v) ] ] ]",
      extContext.nvmContext, true)
    val activation = new Activation(proc, extContext.nvmContext.activation, 0)
    val askTask = extContext.nvmContext.callReporterProcedure(activation).asInstanceOf[nvm.AnonymousCommand]

    val listener = ChangeListener {
      // Can run on AWT event thread, so we have to explicitly submit job to JobThread. BCH 4/21/2015
      case SetPropEvent(`widgetKey`, `propertyKey`, v, _) =>
        runTask(ws, extContext.nvmContext, askTask, Array(widgetKey, task, v))
      case RemoveWidget(`widgetKey`) => OnChange.removeListeners(writer, widgetKey, propertyKey)
      case _ =>
    }

    writer.subscribe(listener)

    OnChange.listeners += (((widgetKey, propertyKey), listener))
  }
}

class OnChange(writer: Writer, kindInfo: KindInfo, wcm: WidgetContextManager) extends OnChangePrim(writer, wcm) {

  override def getSyntax = commandSyntax(List(StringType, CommandType))

  def perform(args: Array[Argument], context: Context): Unit = {
    val widgetKey: WidgetKey = args(0).getString
    val propertyKey: PropertyKey = kindInfo.defaultProperty(widgetKey).key
    addListener(context, widgetKey, propertyKey, args(1).getCommand.asInstanceOf[nvm.AnonymousCommand])
  }
}

class OnChangeProperty(writer: Writer, propertyKey: PropertyKey, wcm: WidgetContextManager)
  extends OnChangePrim(writer, wcm) {

  override def getSyntax = commandSyntax(List(CommandType))

  def perform(args: Array[Argument], context: Context): Unit = {
    addListener(context, wcm.currentContext, propertyKey, args(0).getCommand.asInstanceOf[nvm.AnonymousCommand])
  }
}

