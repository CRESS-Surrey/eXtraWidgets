package uk.ac.surrey.soc.cress.extrawidgets.extension.prim

import org.nlogo.api._
import org.nlogo.api.Context
import org.nlogo.api.DefaultCommand
import org.nlogo.api.LogoList
import org.nlogo.api.Syntax.CommandBlockType
import org.nlogo.api.Syntax.ListType
import org.nlogo.api.Syntax.StringType
import org.nlogo.api.Syntax.commandSyntax
import uk.ac.surrey.soc.cress.extrawidgets.extension.WidgetContextManager
import uk.ac.surrey.soc.cress.extrawidgets.api._

class Ask(wcm: WidgetContextManager)
  extends DefaultCommand
  with HasCommandBlock {
  override def getSyntax = commandSyntax(Array(
    StringType | ListType, CommandBlockType
  ))
  private def runFor(key: WidgetKey, context: Context) =
    wcm.withContext(key) { () ⇒ runBlock(context) }
  def perform(args: Array[Argument], context: Context): Unit =
    args(0).get match {
      case key: String ⇒ runFor(key, context)
      case list: LogoList ⇒
        for (obj ← list) obj match {
          case key: String ⇒ runFor(key, context)
          case _ ⇒ throw XWException(
            "Expected a widget key string but got " + Dump.logoObject(obj) +
              " of type " + Dump.typeName(obj).toUpperCase + " instead.")
        }
    }
}
