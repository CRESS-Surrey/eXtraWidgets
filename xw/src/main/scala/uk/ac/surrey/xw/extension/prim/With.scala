package uk.ac.surrey.xw.extension.prim

import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.Dump
import org.nlogo.api.Reporter
import org.nlogo.core.LogoList
import org.nlogo.core.Syntax
import org.nlogo.core.Syntax.ListType
import org.nlogo.core.Syntax.NormalPrecedence
import org.nlogo.core.Syntax.ReporterType
import org.nlogo.core.Syntax.WildcardType

import uk.ac.surrey.xw.api.WidgetKey
import uk.ac.surrey.xw.api.XWException
import uk.ac.surrey.xw.extension.WidgetContextManager

class With(wcm: WidgetContextManager) extends Reporter {
  override def getSyntax =
    Syntax.reporterSyntax(
      left = ListType,
      right = List(ReporterType),
      ret = WildcardType,
      precedence = NormalPrecedence + 2)
  override def report(args: Array[Argument], context: Context): AnyRef = {
    val task = args(1).getReporter
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
      }.filter(predicate))
  }
}
