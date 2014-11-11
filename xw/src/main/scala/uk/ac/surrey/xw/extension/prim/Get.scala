package uk.ac.surrey.xw.extension.prim

import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.DefaultReporter
import org.nlogo.api.Syntax.StringType
import org.nlogo.api.Syntax.WildcardType
import org.nlogo.api.Syntax.reporterSyntax

import uk.ac.surrey.xw.api.KindName
import uk.ac.surrey.xw.api.PropertyKey
import uk.ac.surrey.xw.api.XWException
import uk.ac.surrey.xw.extension.WidgetContextManager
import uk.ac.surrey.xw.state.Reader

class Get(
  reader: Reader,
  defaultProperties: Map[KindName, PropertyKey],
  wcm: WidgetContextManager)
  extends DefaultReporter {
  override def getSyntax = reporterSyntax(Array(StringType), WildcardType)
  def report(args: Array[Argument], context: Context): AnyRef = {
    val widgetKey = args(0).getString
    val kindName = reader.get("KIND", widgetKey).asInstanceOf[String]
    val propertyKey = defaultProperties.getOrElse(kindName, throw XWException(
      "There is no default property defined for widget kind " + kindName + "."))
    reader.get(propertyKey, widgetKey)
  }
}

