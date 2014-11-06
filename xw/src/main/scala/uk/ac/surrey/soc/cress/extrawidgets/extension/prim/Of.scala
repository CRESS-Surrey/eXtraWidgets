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
import org.nlogo.api.Syntax.StringType
import org.nlogo.api.Syntax.WildcardType

import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKey
import uk.ac.surrey.soc.cress.extrawidgets.api.XWException
import uk.ac.surrey.soc.cress.extrawidgets.extension.WidgetContextManager

class Of(wcm: WidgetContextManager) extends DefaultReporter {
  override def getSyntax =
    Syntax.reporterSyntax(
      ReporterTaskType,
      Array(StringType | ListType),
      WildcardType,
      NormalPrecedence + 1,
      true)
  override def report(args: Array[Argument], context: Context): AnyRef = {
    val task = args(0).getReporterTask
    def reportFor(key: WidgetKey) =
      wcm.withContext(key) { () ⇒ task.report(context, Array[AnyRef]()) }
    args(1).get match {
      case key: WidgetKey ⇒ reportFor(key)
      case list: LogoList ⇒ LogoList.fromVector(
        for (obj ← list.toVector) yield obj match {
          case key: String ⇒ reportFor(key)
          case _ ⇒ throw XWException(
            "Expected a widget key string but got " +
              Dump.logoObject(obj) + " instead.")
        })
    }
  }
}


