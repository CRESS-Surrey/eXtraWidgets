package uk.ac.surrey.xw.extension.prim

import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.DefaultReporter
import org.nlogo.api.Syntax.StringType
import org.nlogo.api.Syntax.reporterSyntax

import uk.ac.surrey.xw.state.Reader

class ToJSON(reader: Reader) extends DefaultReporter {
  override def getSyntax = reporterSyntax(StringType)
  def report(args: Array[Argument], context: Context): AnyRef = reader.toJSON
}
