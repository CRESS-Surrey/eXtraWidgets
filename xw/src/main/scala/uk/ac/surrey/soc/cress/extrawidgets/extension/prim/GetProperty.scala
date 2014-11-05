package uk.ac.surrey.soc.cress.extrawidgets.extension.prim

import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.DefaultReporter
import org.nlogo.api.Syntax.reporterSyntax

import uk.ac.surrey.soc.cress.extrawidgets.extension.WidgetContextProvider
import uk.ac.surrey.soc.cress.extrawidgets.state.Reader

class GetProperty(
  reader: Reader,
  propertyKey: String,
  outputType: Int,
  wcp: WidgetContextProvider)
  extends DefaultReporter {
  override def getSyntax = reporterSyntax(outputType)
  def report(args: Array[Argument], context: Context): AnyRef = {
    val widgetKey = wcp.currentContext
    reader.get(propertyKey, widgetKey)
  }
}
