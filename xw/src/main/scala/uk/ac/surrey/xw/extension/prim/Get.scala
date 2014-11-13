package uk.ac.surrey.xw.extension.prim

import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.DefaultReporter
import org.nlogo.api.Syntax.StringType
import org.nlogo.api.Syntax.WildcardType
import org.nlogo.api.Syntax.reporterSyntax

import uk.ac.surrey.xw.extension.KindInfo
import uk.ac.surrey.xw.extension.WidgetContextManager
import uk.ac.surrey.xw.state.Reader

class Get(
  reader: Reader,
  kindInfo: KindInfo,
  wcm: WidgetContextManager)
  extends DefaultReporter {
  override def getSyntax = reporterSyntax(Array(StringType), WildcardType)
  def report(args: Array[Argument], context: Context): AnyRef = {
    val widgetKey = args(0).getString
    val property = kindInfo.defaultProperty(widgetKey)
    reader.get(property.key, widgetKey)
  }
}

