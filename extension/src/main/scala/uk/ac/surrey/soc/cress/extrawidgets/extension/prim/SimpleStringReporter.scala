package uk.ac.surrey.soc.cress.extrawidgets.extension.prim

import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.DefaultReporter
import org.nlogo.api.Syntax.StringType
import org.nlogo.api.Syntax.reporterSyntax

trait SimpleStringReporter extends DefaultReporter with NamedPrimitive {
  override def getSyntax = reporterSyntax(StringType)
  val string: String
  def report(args: Array[Argument], context: Context): AnyRef = string
}