package uk.ac.surrey.xw.extension.prim

import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.DefaultCommand
import org.nlogo.api.Syntax.StringType
import org.nlogo.api.Syntax.WildcardType
import org.nlogo.api.Syntax.commandSyntax
import uk.ac.surrey.xw.state.Writer
import uk.ac.surrey.xw.extension.WidgetContextManager
import uk.ac.surrey.xw.extension.KindInfo
import uk.ac.surrey.xw.api.XWException

class SetProperty(
  writer: Writer,
  propertyKey: String,
  inputType: Int,
  kindInfo: KindInfo,
  wcm: WidgetContextManager)
  extends DefaultCommand {
  override def getSyntax = commandSyntax(Array(inputType))
  def perform(args: Array[Argument], context: Context): Unit = {

    val widgetKey = wcm.currentContext
    val property = kindInfo.property(propertyKey, widgetKey)

    if (property.readOnly) throw XWException(
      "The " + property.key + " property is read-only " +
        " for widgets of kind " + kindInfo.kindName(widgetKey) + "."
    )

    val propertyValue = property.encode(args(0).get)
    writer.set(propertyKey, widgetKey, propertyValue, fromUI = false)
  }
}
