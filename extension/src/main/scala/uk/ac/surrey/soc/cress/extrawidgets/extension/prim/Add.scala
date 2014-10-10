package uk.ac.surrey.soc.cress.extrawidgets.extension.prim

import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.DefaultCommand
import org.nlogo.api.Syntax.ListType
import org.nlogo.api.Syntax.RepeatableType
import org.nlogo.api.Syntax.StringType
import org.nlogo.api.Syntax.commandSyntax

import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyMap
import uk.ac.surrey.soc.cress.extrawidgets.extension.util.enrichEither
import uk.ac.surrey.soc.cress.extrawidgets.extension.util.enrichVector
import uk.ac.surrey.soc.cress.extrawidgets.state.Writer

class Add(writer: Writer) extends DefaultCommand {
  override def getSyntax = commandSyntax(Array(StringType, ListType | RepeatableType), 1)
  def properties(args: Array[Argument]): PropertyMap =
    Vector(args: _*).tail.flatMap(_.getList.toVector).toPropertyMap
  def perform(args: Array[Argument], context: Context): Unit = {
    val widgetKey = args(0).getString
    writer.add(widgetKey, properties(args)).rightOrThrow
  }
}

class AddWidget(writer: Writer, kindName: String) extends Add(writer) {
  override def properties(args: Array[Argument]) =
    super.properties(args) + ("KIND" -> kindName)
}

