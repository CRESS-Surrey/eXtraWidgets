package uk.ac.surrey.xw.extension.prim

import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.Reporter
import org.nlogo.core.Syntax.StringType
import org.nlogo.core.Syntax.WildcardType
import org.nlogo.core.Syntax.reporterSyntax

import uk.ac.surrey.xw.extension.KindInfo
import uk.ac.surrey.xw.extension.WidgetContextManager
import uk.ac.surrey.xw.state.Reader

class Get(
  reader: Reader,
  kindInfo: KindInfo,
  wcm: WidgetContextManager)
  extends Reporter {
  override def getSyntax = reporterSyntax(right = List(StringType), ret = WildcardType)
  def report(args: Array[Argument], context: Context): AnyRef = {
    val widgetKey = args(0).getString
    val property = kindInfo.defaultProperty(widgetKey)
    reader.get(property.key, widgetKey)
  }
}

