package uk.ac.surrey.soc.cress.extrawidgets.core

import uk.ac.surrey.soc.cress.extrawidgets.state.Writer
import org.nlogo.app.App
import org.nlogo.app.ToolsMenu
import uk.ac.surrey.soc.cress.extrawidgets.state.getOrCreateModel
import uk.ac.surrey.soc.cress.extrawidgets.plugin.ExtraWidgetsPlugin
import Swing._

class Manager(val app: App, val toolsMenu: ToolsMenu) {

  val (reader, writer) = getOrCreateModel(app.workspace.getExtensionManager)

  val gui = new GUI(app.tabs, toolsMenu, writer)
  val view = new View(reader, gui)

  app.frame.onComponentShown { _ ⇒
    (0 until app.tabs.getTabCount)
      .map(i ⇒ i -> app.tabs.getComponentAt(i))
      .find(_._2.isInstanceOf[ExtraWidgetsPlugin])
      .foreach {
        case (i, pluginTab) ⇒
          app.tabs.remove(pluginTab)
          app.tabs.removeMenuItem(i)
      }
  }

  val widgets: Map[String, Class[_]] = WidgetsLoader.loadExtraWidgets()
}
