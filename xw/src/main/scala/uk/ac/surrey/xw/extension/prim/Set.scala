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

    /* we call `property.fromAny` here instead even though
     * property.set is also going to call it later because
     * we want it to fail before we actually write the value
     * to the property map. NP 2014-11-17.
     */
    val value = property.fromAny(propertyValue).asInstanceOf[AnyRef]

    writer.set(property.key, widgetKey, value, true)
  }
}
