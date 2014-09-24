package uk.ac.surrey.soc.cress.extrawidgets.plugin

import org.nlogo.app.App
import org.nlogo.app.ToolsMenu
import javax.swing.JPanel
import uk.ac.surrey.soc.cress.extrawidgets.plugin.controller.Controller
import uk.ac.surrey.soc.cress.extrawidgets.plugin.model._
import uk.ac.surrey.soc.cress.extrawidgets.plugin.util.Swing.enrichComponent
import uk.ac.surrey.soc.cress.extrawidgets.plugin.gui.GUI
import uk.ac.surrey.soc.cress.extrawidgets.plugin.view.View

case class ExtraWidgetsPlugin(val app: App, toolsMenu: ToolsMenu) extends JPanel {

  val widgetMap = getOrCreateWidgetMapIn(app.workspace.getExtensionManager)
  val reader = new Reader(widgetMap)
  val writer = new Writer(widgetMap, reader)
  val controller = new Controller(writer)
  val gui = new GUI(app.tabs, toolsMenu, controller)
  val view = new View(reader, gui)

  app.frame.onComponentShown(_ ⇒ gui.removeTab(this))

}
