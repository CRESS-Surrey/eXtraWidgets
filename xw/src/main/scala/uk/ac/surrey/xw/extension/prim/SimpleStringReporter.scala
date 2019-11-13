package uk.ac.surrey.xw.extension.prim

import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.Reporter
import org.nlogo.core.Syntax.StringType
import org.nlogo.core.Syntax.reporterSyntax

trait SimpleStringReporter extends Reporter {
  override def getSyntax = reporterSyntax(ret = StringType)
  val string: String
  def report(args: Array[Argument], context: Context): AnyRef = string
}