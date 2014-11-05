package uk.ac.surrey.soc.cress.extrawidgets.extension.prim

import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.DefaultReporter
import org.nlogo.api.Syntax
import org.nlogo.api.Syntax.NormalPrecedence
import org.nlogo.api.Syntax.ReporterTaskType
import org.nlogo.api.Syntax.StringType
import org.nlogo.api.Syntax.WildcardType

import uk.ac.surrey.soc.cress.extrawidgets.extension.WidgetContextManager

class Of(wcm: WidgetContextManager) extends DefaultReporter {
  override def getSyntax =
    Syntax.reporterSyntax(
      ReporterTaskType,
      Array(StringType),
      WildcardType,
      NormalPrecedence + 1,
      true)
  override def report(args: Array[Argument], context: Context): AnyRef = {
    val task = args(0).getReporterTask
    val key = args(1).getString
    wcm.withContext(key) { () ⇒
      task.report(context, Array[AnyRef]())
    }
  }
}
