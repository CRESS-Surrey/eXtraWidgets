package uk.ac.surrey.xw.extension.prim

import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.DefaultCommand
import org.nlogo.api.Syntax.StringType
import org.nlogo.api.Syntax.commandSyntax
import uk.ac.surrey.xw.state.Writer
import uk.ac.surrey.xw.state.JSONLoader

class LoadJSON(writer: Writer) extends DefaultCommand {
  override def getSyntax = commandSyntax(Array(StringType))
  val jsonLoader = new JSONLoader(writer)
  def perform(args: Array[Argument], context: Context): Unit = {
    jsonLoader.load(args(0).getString)
  }
}
