package uk.ac.surrey.soc.cress.extrawidgets.plugin

import org.nlogo.app.App
import org.nlogo.app.ToolsMenu

import javax.swing.JPanel
import uk.ac.surrey.soc.cress.extrawidgets.plugin.data.ExtraWidgetsData
import uk.ac.surrey.soc.cress.extrawidgets.plugin.util.Swing.enrichComponent

case class ExtraWidgetsPlugin(val app: App, toolsMenu: ToolsMenu) extends JPanel {

  val data = ExtraWidgetsData.getOrCreateIn(app.workspace.getExtensionManager)

  val tabsManager = new TabsManager(app.tabs, toolsMenu, data)
  app.frame.onComponentShown(_ ⇒ tabsManager.removeTab(this))

}
