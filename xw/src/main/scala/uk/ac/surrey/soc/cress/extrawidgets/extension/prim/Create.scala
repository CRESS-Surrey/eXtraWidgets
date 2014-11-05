package uk.ac.surrey.soc.cress.extrawidgets.extension.prim

import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.DefaultCommand
import org.nlogo.api.Syntax.CommandBlockType
import org.nlogo.api.Syntax.OptionalType
import org.nlogo.api.Syntax.StringType
import org.nlogo.api.Syntax.commandSyntax

import uk.ac.surrey.soc.cress.extrawidgets.extension.WidgetContextManager
import uk.ac.surrey.soc.cress.extrawidgets.state.Writer

class Create(kindName: String, writer: Writer, wcm: WidgetContextManager)
  extends DefaultCommand
  with HasCommandBlock {
  override def getSyntax = commandSyntax(Array(
    StringType, CommandBlockType | OptionalType
  ))
  def perform(args: Array[Argument], context: Context): Unit = {
    val widgetKey = args(0).getString
    writer.add(widgetKey, Map("KIND" -> kindName))
    wcm.withContext(args(0).getString) { () ⇒
      runBlock(context)
    }
  }
}
