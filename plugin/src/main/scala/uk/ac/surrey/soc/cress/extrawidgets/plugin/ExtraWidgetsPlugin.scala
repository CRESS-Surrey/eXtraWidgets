package uk.ac.surrey.soc.cress.extrawidgets.plugin

import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent

import org.nlogo.app.App
import org.nlogo.app.ToolsMenu

import javax.swing.JPanel

class ExtraWidgetsPlugin(app: App, toolsMenu: ToolsMenu) extends JPanel {
  self =>

  val tabsManager: TabsManager = new TabsManager(app.tabs)

  app.frame.addComponentListener(new ComponentAdapter() {
    override def componentShown(e: ComponentEvent) {
      toolsMenu.addSeparator()
      toolsMenu.addMenuItem(GUIStrings.ToolsMenu.CreateTab, 'X', true)
      tabsManager.removeTab(self)
    }
  })
}