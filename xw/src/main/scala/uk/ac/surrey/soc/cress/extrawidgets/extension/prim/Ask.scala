package uk.ac.surrey.soc.cress.extrawidgets.extension.prim

import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.DefaultCommand
import org.nlogo.api.Syntax.CommandBlockType
import org.nlogo.api.Syntax.StringType
import org.nlogo.api.Syntax.commandSyntax

import uk.ac.surrey.soc.cress.extrawidgets.extension.WidgetContextManager

class Ask(wcm: WidgetContextManager)
  extends DefaultCommand
  with HasBlock {
  override def getSyntax = commandSyntax(Array(
    StringType, CommandBlockType
  ))
  def perform(args: Array[Argument], context: Context): Unit =
    wcm.withContext(args(0).getString) { () ⇒
      runBlock(context)
    }
}
