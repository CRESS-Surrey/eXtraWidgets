package uk.ac.surrey.xw.extension.prim

import org.nlogo.api.{Argument, Context, DefaultCommand}
import org.nlogo.api.Syntax.{StringType, commandSyntax, CommandBlockType}
import org.nlogo.nvm.{AssemblerAssistant, ExtensionContext, CustomAssembled, Context => NvmContext}
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

abstract class OnChangePrim(writer: Writer, wcm: WidgetContextManager) extends DefaultCommand with CustomAssembled {

  def addListener(context: Context, widgetKey: WidgetKey, propertyKey: PropertyKey): Unit = {
    val extContext = context.asInstanceOf[ExtensionContext]
    val ws = extContext.workspace.asInstanceOf[AbstractWorkspace]
    val agentSet = ws.world.observers
    // The ip of the block is relative to the ip at *this* point in time. Since the listeners will activate later on,
    // after this Context has changed, the ip will be lost. Thus, we need to compute the block's ip now. This
    // prevents us from using HasCommandBlock to run the blocks. BCH 4/19/2015
    val ip = extContext.nvmContext.ip
    val childContext = new NvmContext(extContext.nvmContext, extContext.workspace.world.observer)
    childContext.agent = ws.world.observer

    OnChange.removeListeners(writer, widgetKey, propertyKey)

    val listener = ChangeListener { _ =>
      // Can run on AWT event thread, so we have explicitly submit job to JobThread. BCH 4/21/2015
      childContext.ip = ip
      ws.jobManager.addJob(childContext.makeConcurrentJob(agentSet), waitForCompletion = false)
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

  def assemble(a: AssemblerAssistant) {
    a.block()
    a.done()
  }
}

class OnChange(writer: Writer, kindInfo: KindInfo, wcm: WidgetContextManager) extends OnChangePrim(writer, wcm) {

  override def getSyntax = commandSyntax(Array(StringType, CommandBlockType))

  def perform(args: Array[Argument], context: Context): Unit = {
    val widgetKey: WidgetKey = args(0).getString
    val propertyKey: PropertyKey = kindInfo.defaultProperty(widgetKey).key
    addListener(context, widgetKey, propertyKey)
  }
}

class OnChangeProperty(writer: Writer, propertyKey: PropertyKey, wcm: WidgetContextManager)
  extends OnChangePrim(writer, wcm) {

  override def getSyntax = commandSyntax(Array(CommandBlockType))

  def perform(args: Array[Argument], context: Context): Unit = {
    addListener(context, wcm.currentContext, propertyKey)
  }
}
