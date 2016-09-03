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
import org.nlogo.core.Syntax.StringType
import org.nlogo.core.Syntax.WildcardType

import uk.ac.surrey.xw.api.WidgetKey
import uk.ac.surrey.xw.api.XWException
import uk.ac.surrey.xw.extension.WidgetContextManager

class Of(wcm: WidgetContextManager) extends Reporter {
  override def getSyntax =
    Syntax.reporterSyntax(
      left = ReporterType,
      right = List(StringType | ListType),
      ret = WildcardType,
      precedence = NormalPrecedence + 1,
      isRightAssociative = true)
  override def report(args: Array[Argument], context: Context): AnyRef = {
    val task = args(0).getReporter
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


