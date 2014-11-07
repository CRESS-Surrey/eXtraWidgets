package uk.ac.surrey.soc.cress.extrawidgets.extension.prim

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

class Set(
  reader: Reader,
  writer: Writer,
  defaultProperties: Map[KindName, PropertyKey],
  wcm: WidgetContextManager)
  extends DefaultCommand {
  override def getSyntax = commandSyntax(Array(StringType, WildcardType))
  def perform(args: Array[Argument], context: Context): Unit = {
    val widgetKey = args(0).getString
    val propertyValue = args(1).get
    val kindName = reader.get("KIND", widgetKey).asInstanceOf[String]
    val propertyKey = defaultProperties.getOrElse(kindName, throw XWException(
      "There is no default property defined for widget kind " + kindName + "."))
    writer.set(propertyKey, widgetKey, propertyValue, true)
  }
}
