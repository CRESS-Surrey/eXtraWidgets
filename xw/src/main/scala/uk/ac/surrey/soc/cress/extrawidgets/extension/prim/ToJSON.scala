package uk.ac.surrey.soc.cress.extrawidgets.extension.prim

import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.DefaultReporter
import org.nlogo.api.Syntax.StringType
import org.nlogo.api.Syntax.reporterSyntax

import uk.ac.surrey.soc.cress.extrawidgets.state.Reader

class ToJSON(reader: Reader) extends DefaultReporter {
  override def getSyntax = reporterSyntax(StringType)
  def report(args: Array[Argument], context: Context): AnyRef = reader.toJSON
}
