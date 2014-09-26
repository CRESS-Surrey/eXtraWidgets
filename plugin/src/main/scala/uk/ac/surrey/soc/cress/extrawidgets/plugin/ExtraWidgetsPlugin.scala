package uk.ac.surrey.soc.cress.extrawidgets.plugin

import org.nlogo.app.App
import org.nlogo.app.ToolsMenu
import javax.swing.JPanel
import uk.ac.surrey.soc.cress.extrawidgets.plugin.controller.Controller
import uk.ac.surrey.soc.cress.extrawidgets.plugin.gui.GUI
import uk.ac.surrey.soc.cress.extrawidgets.plugin.model.getOrCreateModel
import uk.ac.surrey.soc.cress.extrawidgets.plugin.util.ExtraWidgetsPluginException
import uk.ac.surrey.soc.cress.extrawidgets.plugin.util.Swing.enrichComponent
import uk.ac.surrey.soc.cress.extrawidgets.plugin.util.Swing.errorDialog
import uk.ac.surrey.soc.cress.extrawidgets.plugin.view.View
import java.io.File

object ExtraWidgetsPlugin {
  val name = "eXtraWidgets"
}

case class ExtraWidgetsPlugin(val app: App, toolsMenu: ToolsMenu) extends JPanel {

  val (reader, writer) = getOrCreateModel(app.workspace.getExtensionManager)
  val controller = new Controller(writer)
  val gui = new GUI(app.tabs, toolsMenu, controller)
  val view = new View(reader, gui)

  app.frame.onComponentShown(_ ⇒ gui.removeTab(this))

  loadExtraWidgets()

  def loadExtraWidgets(): Unit = {

    val widgetsFolder = new File("widgets")
    val result = Either
      .cond(widgetsFolder.isDirectory, widgetsFolder.listFiles,
        "Can't find extra widgets folder: " + widgetsFolder.getCanonicalPath)
      .right.map {
        for {
          folder ← _
          if folder.isDirectory
          file ← folder.listFiles
          if file.getName.toUpperCase == (folder.getName + ".jar").toUpperCase
        } yield file
      }

    println(result.right.map(_.toList))

  }

}
