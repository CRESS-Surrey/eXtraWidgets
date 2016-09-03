package uk.ac.surrey.xw.extension.prim

import org.nlogo.api.Argument
import org.nlogo.api.Command
import org.nlogo.api.Context
import org.nlogo.core.Syntax.commandSyntax

import uk.ac.surrey.xw.state.Writer

class ClearAll(writer: Writer) extends Command {
  override def getSyntax = commandSyntax()
  def perform(args: Array[Argument], context: Context): Unit =
    writer.clearAll()
}
