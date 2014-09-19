package uk.ac.surrey.soc.cress.extrawidgets.plugin

import org.nlogo.app.AppFrame
import org.nlogo.app.Tabs
import org.nlogo.app.ToolsMenu

import javax.swing.JPanel
import uk.ac.surrey.soc.cress.extrawidgets.plugin.util.Swing.enrichComponent

case class ExtraWidgetsPlugin(
  val appFrame: AppFrame,
  val tabs: Tabs,
  val toolsMenu: ToolsMenu)
  extends JPanel {
  self ⇒

  val tabsManager: TabsManager = new TabsManager(tabs)

  toolsMenu.addSeparator()
  toolsMenu.addMenuItem(GUIStrings.ToolsMenu.CreateTab, 'X', true)

  appFrame.onComponentShown(_ ⇒ tabsManager.removeTab(self))
}