package uk.ac.surrey.soc.cress.extrawidgets.plugin

import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent

import org.nlogo.app.App
import org.nlogo.app.ToolsMenu

import javax.swing.JPanel

class ExtraWidgetsPlugin(app: App) extends JPanel {
  self =>

  private var _tabsManager: TabsManager = null
  def tabsManager = _tabsManager
  private var _toolsMenu: ToolsMenu = null
  def toolsMenu = _toolsMenu

  app.frame.addComponentListener(new ComponentAdapter() {
    override def componentShown(e: ComponentEvent) {
      val jMenuBar = app.frame.getJMenuBar
      _toolsMenu =
        (0 until jMenuBar.getMenuCount)
          .map(jMenuBar.getMenu)
          .collect { case m: ToolsMenu => m }
          .headOption
          .getOrElse(throw new IllegalStateException(GUIStrings.Errors.CantFindToolsMenu))
      firePropertyChange("toolsMenu", null, _toolsMenu)
      toolsMenu.addSeparator()
      toolsMenu.addMenuItem(GUIStrings.ToolsMenu.CreateTab, 'X', true)
      _tabsManager = new TabsManager(app.tabs)
      firePropertyChange("tabsManager", null, _tabsManager)
      tabsManager.removeTab(self)
    }
  })
}