package uk.ac.surrey.xw.extension.prim

import org.nlogo.api.Argument
import org.nlogo.api.Command
import org.nlogo.api.Context
import org.nlogo.core.Syntax.CommandBlockType
import org.nlogo.core.Syntax.OptionalType
import org.nlogo.core.Syntax.StringType
import org.nlogo.core.Syntax.commandSyntax

import uk.ac.surrey.xw.extension.WidgetContextManager
import uk.ac.surrey.xw.state.Writer

class Create(kindName: String, writer: Writer, wcm: WidgetContextManager)
  extends Command
  with HasCommandBlock {
  override def getSyntax = commandSyntax(List(
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
