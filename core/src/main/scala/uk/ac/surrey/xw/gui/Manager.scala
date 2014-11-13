package uk.ac.surrey.xw.gui

import java.awt.Toolkit.getDefaultToolkit
import java.awt.event.InputEvent.SHIFT_MASK

import org.nlogo.app.App
import org.nlogo.app.ToolsMenu
import org.nlogo.swing.RichAction

import javax.swing.JMenuItem
import javax.swing.KeyStroke.getKeyStroke
import uk.ac.surrey.xw.api.WidgetKind
import uk.ac.surrey.xw.api.XWException
import uk.ac.surrey.xw.state.Reader
import uk.ac.surrey.xw.state.Writer

class CreateTabMenuItem(val manager: Manager)
  extends JMenuItem(RichAction(Strings.CreateTab)(_ ⇒ manager.gui.createNewTab())) {
  setIcon(null)
  setAccelerator(getKeyStroke(
    'X', SHIFT_MASK | getDefaultToolkit.getMenuShortcutKeyMask))
}

class Manager(
  val app: App,
  val reader: Reader,
  val writer: Writer,
  val widgetKinds: Map[String, WidgetKind[_]]) {

  val gui = new GUI(app, writer, widgetKinds)

  val toolsMenu = {
    val menuBar = app.frame.getJMenuBar
    (0 until menuBar.getMenuCount)
      .map(menuBar.getMenu)
      .collectFirst {
        case toolsMenu: ToolsMenu ⇒ toolsMenu
      }
      .getOrElse(throw new XWException("Can't find Tools menu."))
  }

  toolsMenu.addSeparator()
  toolsMenu.add(new CreateTabMenuItem(this))

}
