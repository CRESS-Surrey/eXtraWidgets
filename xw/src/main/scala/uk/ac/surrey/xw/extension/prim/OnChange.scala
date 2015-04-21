package uk.ac.surrey.xw.extension.prim

import org.nlogo.api.{Argument, Context, DefaultCommand}
import org.nlogo.api.Syntax.{StringType, commandSyntax, CommandBlockType}
import org.nlogo.nvm.{AssemblerAssistant, ExtensionContext, CustomAssembled, Context => NvmContext}
import uk.ac.surrey.xw.api.{PropertyKey, WidgetKey}
import uk.ac.surrey.xw.extension.{KindInfo, WidgetContextManager}
import uk.ac.surrey.xw.state.{StateEvent, Writer, SetProperty => SetPropEvent}

import scala.collection.mutable.{Publisher, Subscriber, Map => MutableMap}

case class ChangeListener(func: StateEvent => Unit)  extends Subscriber[StateEvent, Publisher[StateEvent]] {
  def notify(pub: Publisher[StateEvent], event: StateEvent): Unit = func(event)
}

object OnChange {
  val listeners = MutableMap.empty[(WidgetKey, PropertyKey), ChangeListener]
}

abstract class OnChangePrim(writer: Writer, wcm: WidgetContextManager) extends DefaultCommand with CustomAssembled {

  def addListener(context: Context, widgetKey: WidgetKey, propertyKey: PropertyKey): Unit = {
    val extContext = context.asInstanceOf[ExtensionContext]
    val ws = extContext.workspace
    val agentSet = ws.world.observers
    // The ip of the block is relative to the ip at *this* point in time. Since the listeners will activate later on,
    // after this Context has changed, the ip will be lost. Thus, we need to compute the block's ip now. This
    // prevents us from using HasCommandBlock to run the blocks. BCH 4/19/2015
    val ip = extContext.nvmContext.ip
    val childContext = new NvmContext(extContext.nvmContext, extContext.workspace.world.observer)
    childContext.agent = ws.world.observer

    val listener = ChangeListener { _ =>
      childContext.ip = ip
      ws.addJobFromJobThread(childContext.makeConcurrentJob(agentSet))
    }

    OnChange.listeners.get((widgetKey, propertyKey)).foreach(writer.removeSubscription)

    writer.subscribe(listener, {
      case SetPropEvent(`widgetKey`, `propertyKey`, _, true) => true
      case _ => false
    })

    OnChange.listeners((widgetKey, propertyKey)) = listener
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
