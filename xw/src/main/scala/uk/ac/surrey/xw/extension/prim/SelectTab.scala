package uk.ac.surrey.xw.extension.prim

import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.DefaultCommand
import org.nlogo.api.Syntax.NumberType
import org.nlogo.api.Syntax.StringType
import org.nlogo.api.Syntax.commandSyntax
import org.nlogo.awt.EventQueue.invokeLater
import org.nlogo.window.GUIWorkspace
import org.nlogo.workspace.AbstractWorkspace

import uk.ac.surrey.xw.api.RichWorkspace.enrichWorkspace
import uk.ac.surrey.xw.api.XWException
import uk.ac.surrey.xw.api.toRunnable
import uk.ac.surrey.xw.state.Reader

class SelectTab(reader: Reader, ws: AbstractWorkspace) extends DefaultCommand {
  override def getSyntax = commandSyntax(Array(NumberType | StringType))
  def perform(args: Array[Argument], context: Context): Unit =
    ws match {
      case guiWS: GUIWorkspace ⇒
        args(0).get match {
          case n: java.lang.Number ⇒
            val i = n.intValue - 1
            if (i < 0 || i >= guiWS.tabs.getTabCount) throw XWException(
              "Invalid tab index: " + n.intValue + ".")
            invokeLater { guiWS.tabs.setSelectedIndex(i) }
          case s: java.lang.String ⇒
            val tab = guiWS.xwTabs.find(_.key == s).getOrElse(throw XWException(
              "Unknown tab key: " + s + "."))
            invokeLater { guiWS.tabs.setSelectedComponent(tab) }
        }
    }
}
