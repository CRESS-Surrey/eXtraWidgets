package uk.ac.surrey.soc.cress.extrawidgets.extension.prim

import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.DefaultCommand
import org.nlogo.api.Syntax.StringType
import org.nlogo.api.Syntax.commandSyntax

import uk.ac.surrey.soc.cress.extrawidgets.extension.util.tryTo
import uk.ac.surrey.soc.cress.extrawidgets.plugin.data.MutableExtraWidgetsData

class Add(data: MutableExtraWidgetsData) extends DefaultCommand with NamedPrimitive {
  val primitiveName = "add"
  override def getSyntax = commandSyntax(Array(StringType, StringType))
  def perform(args: Array[Argument], context: Context): Unit = {
    val kind = args(0).getString
    val name = args(1).getString
    tryTo(data.add(kind, name))
  }
}
