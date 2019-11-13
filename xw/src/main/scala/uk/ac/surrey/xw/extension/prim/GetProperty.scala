package uk.ac.surrey.xw.extension.prim

import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.Reporter
import org.nlogo.core.Syntax.reporterSyntax

import uk.ac.surrey.xw.extension.WidgetContextManager
import uk.ac.surrey.xw.state.Reader

class GetProperty(
  reader: Reader,
  propertyKey: String,
  outputType: Int,
  wcm: WidgetContextManager)
  extends Reporter {
  override def getSyntax = reporterSyntax(ret = outputType)
  def report(args: Array[Argument], context: Context): AnyRef = {
    val widgetKey = wcm.currentContext
    reader.get(propertyKey, widgetKey)
  }
}
