package uk.ac.surrey.soc.cress.extrawidgets.extension.prim

import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.DefaultCommand
import org.nlogo.api.Syntax.StringType
import org.nlogo.api.Syntax.commandSyntax

import uk.ac.surrey.soc.cress.extrawidgets.extension.util.tryTo
import uk.ac.surrey.soc.cress.extrawidgets.state.Writer

class Add(writer: Writer) extends DefaultCommand {
  override def getSyntax = commandSyntax(Array(StringType, StringType))
  def perform(args: Array[Argument], context: Context): Unit = {
    val widgetKind = args(0).getString
    val widgetKey = args(1).getString
    tryTo(writer.add(widgetKind, widgetKey))
  }
}
