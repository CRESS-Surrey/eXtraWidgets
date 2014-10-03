package uk.ac.surrey.soc.cress.extrawidgets.extension.prim

import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.DefaultReporter
import org.nlogo.api.Syntax.StringType
import org.nlogo.api.Syntax.WildcardType
import org.nlogo.api.Syntax.reporterSyntax

import uk.ac.surrey.soc.cress.extrawidgets.extension.util.tryTo
import uk.ac.surrey.soc.cress.extrawidgets.state.Reader

class Get(reader: Reader) extends DefaultReporter {
  override def getSyntax = reporterSyntax(Array(StringType, StringType), WildcardType)
  def report(args: Array[Argument], context: Context): AnyRef = {
    val propertyKey = args(0).getString
    val widgetKey = args(1).getString
    tryTo(reader.get(propertyKey, widgetKey))
  }
}
