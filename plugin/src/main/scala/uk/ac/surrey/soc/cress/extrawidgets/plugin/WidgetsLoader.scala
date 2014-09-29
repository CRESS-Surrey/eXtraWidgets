package uk.ac.surrey.soc.cress.extrawidgets.plugin

import org.nlogo.window.RuntimeErrorDialog
import java.io.File
import scala.Array.canBuildFrom
import File.separator

object WidgetsLoader {

  val netLogoPluginsFolderName = "plugins"
  val pluginFolderName = ExtraWidgetsPlugin.name
  val widgetsFolderName = "widgets"

  def loadExtraWidgets(): Unit = {

    try {
      val widgetsFolder = new File("widgets")
      val result =
        getWidgetsFolder
          .right.map(_.listFiles)
          .right.map {
            for {
              folder ← _
              if folder.isDirectory
              file ← folder.listFiles
              if file.getName.toUpperCase == (folder.getName + ".jar").toUpperCase
              url = new java.net.URL(file.getCanonicalPath)
            } yield url
          }

      println(result.right.map(_.toList))
    } catch {
      case e: Exception ⇒ {
        println(e)
        RuntimeErrorDialog.show("eXtraWidgets Plugin",
          null, // context
          null, // instruction
          Thread.currentThread, // thread
          e)
      }
    }

  }

  def getWidgetsFolder: Either[String, File] = {
    val pathFromNetLogo =
      netLogoPluginsFolderName + separator + pluginFolderName + separator + widgetsFolderName
    Option(new File(pathFromNetLogo))
      .filter(_.isDirectory())
      .orElse(Some(new File(widgetsFolderName)))
      .filter(_.isDirectory())
      .toRight("Can't find extra widgets folder.")
  }

}