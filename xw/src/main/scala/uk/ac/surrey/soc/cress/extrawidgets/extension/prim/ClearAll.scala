package uk.ac.surrey.soc.cress.extrawidgets.extension.prim

import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.DefaultCommand
import org.nlogo.api.Syntax.commandSyntax

import uk.ac.surrey.soc.cress.extrawidgets.state.Writer

class ClearAll(writer: Writer) extends DefaultCommand {
  override def getSyntax = commandSyntax
  def perform(args: Array[Argument], context: Context): Unit =
    writer.clearAll()
}
