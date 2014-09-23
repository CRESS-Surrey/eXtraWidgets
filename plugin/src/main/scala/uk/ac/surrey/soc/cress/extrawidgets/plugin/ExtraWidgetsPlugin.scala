package uk.ac.surrey.soc.cress.extrawidgets.plugin

import org.nlogo.app.App
import org.nlogo.app.ToolsMenu
import javax.swing.JPanel
import uk.ac.surrey.soc.cress.extrawidgets.plugin.controller.Controller
import uk.ac.surrey.soc.cress.extrawidgets.plugin.model._
import uk.ac.surrey.soc.cress.extrawidgets.plugin.util.Swing.enrichComponent
import uk.ac.surrey.soc.cress.extrawidgets.plugin.controller.TabsManager

case class ExtraWidgetsPlugin(val app: App, toolsMenu: ToolsMenu) extends JPanel {

  val store = getOrCreateStoreIn(app.workspace.getExtensionManager)
  val writer = new Writer(store)
  val controller = new Controller(writer)
  val tabsManager = new TabsManager(app.tabs, toolsMenu, controller)

  app.frame.onComponentShown(_ ⇒ tabsManager.removeTab(this))

}
