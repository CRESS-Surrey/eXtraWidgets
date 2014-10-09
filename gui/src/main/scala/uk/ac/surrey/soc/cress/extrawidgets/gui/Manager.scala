package uk.ac.surrey.soc.cress.extrawidgets.gui

import org.nlogo.app.App
import org.nlogo.app.ToolsMenu
import uk.ac.surrey.soc.cress.extrawidgets.gui.Swing.enrichComponent
import uk.ac.surrey.soc.cress.extrawidgets.plugin.ExtraWidgetsPlugin
import uk.ac.surrey.soc.cress.extrawidgets.plugin.exceptionDialog
import uk.ac.surrey.soc.cress.extrawidgets.state.getOrCreateModel
import uk.ac.surrey.soc.cress.extrawidgets.core.WidgetsLoader
import uk.ac.surrey.soc.cress.extrawidgets.core.WidgetKind

class Manager(val app: App, val toolsMenu: ToolsMenu) {

  val (reader, writer) = getOrCreateModel(app.workspace.getExtensionManager)

  val widgetKinds: Map[String, WidgetKind] =
    WidgetsLoader.loadWidgetKinds().fold(
      exceptions ⇒ { exceptions.foreach(exceptionDialog); Map.empty },
      identity
    )

  val gui = new GUI(app, toolsMenu, writer, widgetKinds)
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

}
