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
import uk.ac.surrey.xw.api.XWException
import uk.ac.surrey.xw.api.XWException

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

    if (property.readOnly) throw XWException(
      "The " + property.key + " property is read-only " +
        " for widgets of kind " + kindInfo.kindName(widgetKey) + "."
    )

    val value =
      try property.encode(propertyValue)
      catch { case e: IllegalArgumentException ⇒ throw XWException(e.getMessage, e) }

    writer.set(property.key, widgetKey, value, fromUI = false)
  }
}
