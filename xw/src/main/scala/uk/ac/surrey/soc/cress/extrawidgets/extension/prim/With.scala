package uk.ac.surrey.soc.cress.extrawidgets.extension.prim

import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.DefaultReporter
import org.nlogo.api.Dump
import org.nlogo.api.LogoList
import org.nlogo.api.Syntax
import org.nlogo.api.Syntax.ListType
import org.nlogo.api.Syntax.NormalPrecedence
import org.nlogo.api.Syntax.ReporterTaskType
import org.nlogo.api.Syntax.WildcardType

import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKey
import uk.ac.surrey.soc.cress.extrawidgets.api.XWException
import uk.ac.surrey.soc.cress.extrawidgets.extension.WidgetContextManager

class With(wcm: WidgetContextManager) extends DefaultReporter {
  override def getSyntax =
    Syntax.reporterSyntax(
      ListType,
      Array(ReporterTaskType),
      WildcardType,
      NormalPrecedence + 2,
      false) // left associative
  override def report(args: Array[Argument], context: Context): AnyRef = {
    val task = args(1).getReporterTask
    def predicate(key: WidgetKey): Boolean =
      wcm.withContext(key) { () ⇒
        task.report(context, Array[AnyRef]()) match {
          case b: java.lang.Boolean ⇒ b
          case obj ⇒ throw XWException(
            "Expected a true/false value but got " +
              Dump.logoObject(obj) + " instead.")
        }
      }
    LogoList.fromVector(
      args(0).getList.toVector.collect {
        case key: String ⇒ key
        case obj ⇒ throw XWException(
          "Expected a widget key string but got " +
            Dump.logoObject(obj) + " instead.")
      }.filter(predicate)
    )
  }
}
