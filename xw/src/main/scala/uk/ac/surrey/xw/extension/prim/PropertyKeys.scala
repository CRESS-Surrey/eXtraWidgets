package uk.ac.surrey.xw.extension.prim

import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.DefaultReporter
import org.nlogo.api.LogoList
import org.nlogo.api.Syntax.ListType
import org.nlogo.api.Syntax.StringType
import org.nlogo.api.Syntax.reporterSyntax

import uk.ac.surrey.xw.api.enrichEither
import uk.ac.surrey.xw.state.Reader

class PropertyKeys(reader: Reader) extends DefaultReporter {
  override def getSyntax = reporterSyntax(Array(StringType), ListType)
  def report(args: Array[Argument], context: Context): AnyRef = {
    val widgetKey = args(0).getString
    LogoList.fromVector(reader.propertyKeyVector(widgetKey).rightOrThrow)
  }
}
