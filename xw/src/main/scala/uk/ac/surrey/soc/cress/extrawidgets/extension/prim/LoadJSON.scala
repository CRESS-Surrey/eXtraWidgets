package uk.ac.surrey.soc.cress.extrawidgets.extension.prim

import uk.ac.surrey.soc.cress.extrawidgets.state.Writer
import org.nlogo.api.DefaultCommand

import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.DefaultCommand
import org.nlogo.api.Syntax.StringType
import org.nlogo.api.Syntax.WildcardType
import org.nlogo.api.Syntax.commandSyntax

import uk.ac.surrey.soc.cress.extrawidgets.api.KindName
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyKey
import uk.ac.surrey.soc.cress.extrawidgets.api.XWException
import uk.ac.surrey.soc.cress.extrawidgets.extension.WidgetContextManager
import uk.ac.surrey.soc.cress.extrawidgets.state.Reader
import uk.ac.surrey.soc.cress.extrawidgets.state.Writer

class LoadJSON(writer: Writer) extends DefaultCommand {
  override def getSyntax = commandSyntax(Array(StringType))
  def perform(args: Array[Argument], context: Context): Unit = {
    writer.loadJSON(args(0).getString)
  }
}
