package uk.ac.surrey.soc.cress.extrawidgets.plugin

import org.nlogo.app.ToolsMenu
import org.nlogo.app.App
import uk.ac.surrey.soc.cress.extrawidgets.plugin.controller.Controller
import uk.ac.surrey.soc.cress.extrawidgets.plugin.gui.GUI
import uk.ac.surrey.soc.cress.extrawidgets.plugin.util.Swing.enrichComponent
import uk.ac.surrey.soc.cress.extrawidgets.plugin.view.View
import uk.ac.surrey.soc.cress.extrawidgets.state.getOrCreateModel

class ExtraWidgetsManager(val app: App, val toolsMenu: ToolsMenu) {
  val (reader, writer) = getOrCreateModel(app.workspace.getExtensionManager)
  val controller = new Controller(writer)
  val gui = new GUI(app.tabs, toolsMenu, controller)
  val view = new View(reader, gui)
  val widgets: Map[String, Class[_]] = WidgetsLoader.loadExtraWidgets()
}
