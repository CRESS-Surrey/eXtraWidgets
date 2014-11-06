package uk.ac.surrey.soc.cress.extrawidgets.extension.prim

import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.DefaultCommand
import org.nlogo.api.Dump
import org.nlogo.api.LogoList
import org.nlogo.api.LogoList.toIterator
import org.nlogo.api.Syntax.CommandBlockType
import org.nlogo.api.Syntax.ListType
import org.nlogo.api.Syntax.StringType
import org.nlogo.api.Syntax.commandSyntax

import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKey
import uk.ac.surrey.soc.cress.extrawidgets.api.XWException
import uk.ac.surrey.soc.cress.extrawidgets.extension.WidgetContextManager

class Ask(wcm: WidgetContextManager)
  extends DefaultCommand
  with HasCommandBlock {
  override def getSyntax = commandSyntax(Array(
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
            "Expected a widget key string but got " + Dump.logoObject(obj) +
              " of type " + Dump.typeName(obj).toUpperCase + " instead.")
        }
    }
  }
}
