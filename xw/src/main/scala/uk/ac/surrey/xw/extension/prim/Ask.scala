package uk.ac.surrey.xw.extension.prim

import org.nlogo.api.Argument
import org.nlogo.api.Command
import org.nlogo.api.Context
import org.nlogo.api.Dump
import org.nlogo.core.LogoList
import org.nlogo.core.Syntax.CommandBlockType
import org.nlogo.core.Syntax.ListType
import org.nlogo.core.Syntax.StringType
import org.nlogo.core.Syntax.commandSyntax

import uk.ac.surrey.xw.api.WidgetKey
import uk.ac.surrey.xw.api.XWException
import uk.ac.surrey.xw.extension.WidgetContextManager

class Ask(wcm: WidgetContextManager)
  extends Command
  with HasCommandBlock {
  override def getSyntax = commandSyntax(List(
    StringType | ListType, CommandBlockType
  ))
  def perform(args: Array[Argument], context: Context): Unit = {
    def runFor(key: WidgetKey) =
      wcm.withContext(key) { () ⇒ runBlock(context) }
    args(0).get match {
      case key: WidgetKey ⇒ runFor(key)
      case list: LogoList ⇒
        for (obj ← list) obj match {
          case key: String ⇒ runFor(key)
          case _ ⇒ throw XWException(
            "Expected a widget key string but got " +
              Dump.logoObject(obj) + " instead.")
        }
    }
  }
}
