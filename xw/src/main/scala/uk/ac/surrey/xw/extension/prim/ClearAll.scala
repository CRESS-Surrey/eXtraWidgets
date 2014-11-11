package uk.ac.surrey.xw.extension.prim

import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.DefaultCommand
import org.nlogo.api.Syntax.commandSyntax

import uk.ac.surrey.xw.state.Writer

class ClearAll(writer: Writer) extends DefaultCommand {
  override def getSyntax = commandSyntax
  def perform(args: Array[Argument], context: Context): Unit =
    writer.clearAll()
}
