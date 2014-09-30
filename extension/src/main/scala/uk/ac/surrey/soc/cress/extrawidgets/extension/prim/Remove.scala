package uk.ac.surrey.soc.cress.extrawidgets.extension.prim

import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.DefaultCommand
import org.nlogo.api.Syntax.StringType
import org.nlogo.api.Syntax.commandSyntax

import uk.ac.surrey.soc.cress.extrawidgets.state.Writer

class Remove(writer: Writer) extends DefaultCommand {
  override def getSyntax = commandSyntax(Array(StringType))
  def perform(args: Array[Argument], context: Context): Unit = {
    val id = args(0).getString
    writer.remove(id)
  }
}
