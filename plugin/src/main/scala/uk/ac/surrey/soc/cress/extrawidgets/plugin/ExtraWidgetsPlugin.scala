package uk.ac.surrey.soc.cress.extrawidgets.plugin

import org.nlogo.app.App
import org.nlogo.app.ToolsMenu
import javax.swing.JPanel
import uk.ac.surrey.soc.cress.extrawidgets.plugin.controller.Controller
import uk.ac.surrey.soc.cress.extrawidgets.plugin.gui.GUI
import uk.ac.surrey.soc.cress.extrawidgets.plugin.util.Swing._
import uk.ac.surrey.soc.cress.extrawidgets.plugin.view.View
import uk.ac.surrey.soc.cress.extrawidgets.state.getOrCreateModel
import util._
import java.net.URLClassLoader

object ExtraWidgetsPlugin {
  val name = "eXtraWidgets"
}

class ExtraWidgetsPlugin(val app: App, val toolsMenu: ToolsMenu) extends JPanel {

  val manager =
    getPluginJarFile.right
      .map { classLoader ⇒
        val manager = new ExtraWidgetsManager(app, toolsMenu)
        app.frame.onComponentShown(_ ⇒ manager.gui.removeTab(this))
        manager
      }

  manager.left.foreach(exceptionDialog)
}
