package uk.ac.surrey.xw.extension.prim

import org.nlogo.api.Argument
import org.nlogo.api.Command
import org.nlogo.api.Context
import org.nlogo.awt.EventQueue.invokeAndWait
import org.nlogo.core.Syntax.NumberType
import org.nlogo.core.Syntax.StringType
import org.nlogo.core.Syntax.commandSyntax
import org.nlogo.window.GUIWorkspace
import org.nlogo.workspace.AbstractWorkspace

import uk.ac.surrey.xw.api.RichWorkspace.enrichWorkspace
import uk.ac.surrey.xw.api.XWException
import uk.ac.surrey.xw.api.toRunnable
import uk.ac.surrey.xw.state.Reader

class SelectTab(reader: Reader, workspace: Option[AbstractWorkspace]) extends Command {
  override def getSyntax = commandSyntax(right = List(NumberType | StringType))
  def perform(args: Array[Argument], context: Context): Unit =
    workspace match {
      case Some(guiWS: GUIWorkspace) =>
        args(0).get match {
          case n: java.lang.Number =>
            onGUIThread {
              val i = n.intValue - 1
              if (i < 0 || i >= guiWS.tabs.getTabCount) throw XWException(
                "Invalid tab index: " + n.intValue + ".")
              guiWS.tabs.setSelectedIndex(i)
            }
          case s: java.lang.String =>
            if (!reader.contains(s) || reader.get(reader.kindPropertyKey, s) != reader.tabKindName)
              throw XWException("Unknown tab key: " + s + ".")
            onGUIThread {
              val tab = guiWS.xwTabs.find(_.key == s).getOrElse(throw XWException(
                "Tab exists in xw state but has not been added to the NetLogo GUI: " + s + "."))
              guiWS.tabs.setSelectedComponent(tab)
            }
        }
      case _ => // we're most likely headless, do nothing...
    }

  private def onGUIThread(action: => Unit): Unit = {
    var failure: Option[Exception] = None
    try {
      // Tab creation is queued on the Swing event thread.  Waiting here makes
      // `xw:create-tab ... xw:select-tab ...` reliable in one NetLogo job,
      // which is the documented startup use case for this primitive.
      invokeAndWait {
        try action
        catch {
          case e: Exception => failure = Some(e)
        }
      }
    } catch {
      case e: InterruptedException =>
        Thread.currentThread.interrupt()
        throw XWException("Interrupted while selecting NetLogo tab.", e)
    }
    failure.foreach {
      case e: XWException => throw e
      case e => throw XWException(e.getMessage, e)
    }
  }
}
