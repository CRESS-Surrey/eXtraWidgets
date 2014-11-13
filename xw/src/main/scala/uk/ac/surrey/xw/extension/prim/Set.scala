package uk.ac.surrey.xw.extension.prim

import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.DefaultCommand
import org.nlogo.api.Syntax.StringType
import org.nlogo.api.Syntax.WildcardType
import org.nlogo.api.Syntax.commandSyntax

import uk.ac.surrey.xw.extension.KindInfo
import uk.ac.surrey.xw.extension.WidgetContextManager
import uk.ac.surrey.xw.state.Writer

class Set(
  writer: Writer,
  kindInfo: KindInfo,
  wcm: WidgetContextManager)
  extends DefaultCommand {
  override def getSyntax = commandSyntax(Array(StringType, WildcardType))
  def perform(args: Array[Argument], context: Context): Unit = {
    val widgetKey = args(0).getString
    val propertyValue = args(1).get

    val property = kindInfo.defaultProperty(widgetKey)

    // TODO: check property syntax

    writer.set(property.key, widgetKey, propertyValue, true)
  }
}
