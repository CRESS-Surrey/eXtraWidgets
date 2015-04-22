package uk.ac.surrey.xw.extension.prim

import org.nlogo.api.{Argument, Context, DefaultCommand}
import org.nlogo.api.Syntax.{StringType, commandSyntax, CommandBlockType, CommandTaskType}
import org.nlogo.nvm.{Context => NvmContext, _}
import org.nlogo.workspace.AbstractWorkspace
import uk.ac.surrey.xw.api.{PropertyKey, WidgetKey}
import uk.ac.surrey.xw.extension.{KindInfo, WidgetContextManager}
import uk.ac.surrey.xw.state.{SetProperty => SetPropEvent, RemoveWidget, StateEvent, Writer}

import scala.collection.mutable.{Publisher, Subscriber}
import scala.collection.parallel.mutable.ParMap

case class ChangeListener(func: StateEvent => Unit)  extends Subscriber[StateEvent, Publisher[StateEvent]] {
  def notify(pub: Publisher[StateEvent], event: StateEvent): Unit = func(event)
}

object OnChange {
  val listeners = ParMap.empty[(WidgetKey, PropertyKey), Seq[ChangeListener]]
  def removeListeners(writer: Writer, wk: WidgetKey, pk: PropertyKey) =
    listeners.get((wk, pk)).foreach(_.foreach(writer.removeSubscription))
}

abstract class OnChangePrim(writer: Writer, wcm: WidgetContextManager) extends DefaultCommand {

  def addListener(context: Context, widgetKey: WidgetKey, propertyKey: PropertyKey, task: CommandTask): Unit = {
    val extContext = context.asInstanceOf[ExtensionContext]
    val ws = extContext.workspace.asInstanceOf[AbstractWorkspace]
    OnChange.removeListeners(writer, widgetKey, propertyKey)

    val listener = ChangeListener {
      // Can run on AWT event thread, so we have explicitly submit job to JobThread. BCH 4/21/2015
      case SetPropEvent(_, _, v, _) => runTask(ws, extContext.nvmContext, task, Array(widgetKey, v))
      case _ =>
    }

    val removalListener: ChangeListener = ChangeListener { _ =>
      OnChange.removeListeners(writer, widgetKey, propertyKey)
    }

    writer.subscribe(listener, {
      case SetPropEvent(`widgetKey`, `propertyKey`, _, true) => true
      case _ => false
    })

    writer.subscribe(removalListener, {
      case RemoveWidget(`widgetKey`) => true
      case _ => false
    })

    OnChange.listeners += ((widgetKey, propertyKey), Seq(listener, removalListener))
  }

  def runTask(workspace: AbstractWorkspace, context: NvmContext, task: CommandTask, args: Array[AnyRef]): Unit = {
    val childContext = new NvmContext(context, workspace.world.observers)
    childContext.letBindings = task.lets
    task.bindArgs(childContext, args)
    childContext.activation = new Activation(task.procedure, childContext.activation, 0)
    childContext.activation.args = task.locals
    childContext.ip = -1 // makeConcurrentJob increments the ip and we want to start at 0
    // Since this has to be run as a top level job, we use ConcurrentJob. BCH 4/22/2015
    workspace.jobManager.addJob(childContext.makeConcurrentJob(workspace.world.observers), waitForCompletion = false)
  }
}

class OnChange(writer: Writer, kindInfo: KindInfo, wcm: WidgetContextManager) extends OnChangePrim(writer, wcm) {

  override def getSyntax = commandSyntax(Array(StringType, CommandTaskType))

  def perform(args: Array[Argument], context: Context): Unit = {
    val widgetKey: WidgetKey = args(0).getString
    val propertyKey: PropertyKey = kindInfo.defaultProperty(widgetKey).key
    addListener(context, widgetKey, propertyKey, args(1).getCommandTask.asInstanceOf[CommandTask])
  }
}

class OnChangeProperty(writer: Writer, propertyKey: PropertyKey, wcm: WidgetContextManager)
  extends OnChangePrim(writer, wcm) {

  override def getSyntax = commandSyntax(Array(CommandTaskType))

  def perform(args: Array[Argument], context: Context): Unit = {
    addListener(context, wcm.currentContext, propertyKey, args(0).getCommandTask.asInstanceOf[CommandTask])
  }
}

