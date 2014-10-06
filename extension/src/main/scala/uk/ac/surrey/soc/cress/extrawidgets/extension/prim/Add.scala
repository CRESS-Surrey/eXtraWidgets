package uk.ac.surrey.soc.cress.extrawidgets.extension.prim

import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.DefaultCommand
import org.nlogo.api.Syntax.ListType
import org.nlogo.api.Syntax.StringType
import org.nlogo.api.Syntax.commandSyntax

import uk.ac.surrey.soc.cress.extrawidgets.extension.util.enrichLogoList
import uk.ac.surrey.soc.cress.extrawidgets.extension.util.tryTo
import uk.ac.surrey.soc.cress.extrawidgets.state.Writer

class Add(writer: Writer) extends DefaultCommand {
  override def getSyntax = commandSyntax(Array(StringType, ListType))
  def perform(args: Array[Argument], context: Context): Unit = {
    val widgetKey = args(0).getString
    val properties = args(1).getList.toPropertyMap
    tryTo(writer.add(widgetKey, properties))
  }
}
