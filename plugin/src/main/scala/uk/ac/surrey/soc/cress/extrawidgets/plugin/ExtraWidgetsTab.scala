package uk.ac.surrey.soc.cress.extrawidgets.plugin

import org.nlogo.app.App
import org.nlogo.awt.EventQueue.invokeLater
import org.nlogo.swing.Implicits.thunk2runnable

import javax.swing.JPanel

class ExtraWidgetsPlugin(app: App) extends JPanel {
  val tabsManager = new TabsManager(app.tabs)
  invokeLater { () ⇒ tabsManager.removeTab(this) }
}
