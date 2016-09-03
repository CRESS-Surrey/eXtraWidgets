package uk.ac.surrey.xw.extension.prim

import org.nlogo.api.Argument
import org.nlogo.api.Command
import org.nlogo.api.Context
import org.nlogo.core.Syntax.StringType
import org.nlogo.core.Syntax.commandSyntax

import uk.ac.surrey.xw.state.Writer

class Remove(writer: Writer) extends Command {
  override def getSyntax = commandSyntax(right = List(StringType))
  def perform(args: Array[Argument], context: Context): Unit = {
    val widgetKey = args(0).getString
    writer.remove(widgetKey)
  }
}
