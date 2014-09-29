package uk.ac.surrey.soc.cress.extrawidgets.plugin

import java.io.File
import java.io.File.separator

import scala.Array.canBuildFrom

import uk.ac.surrey.soc.cress.extrawidgets.plugin.util.Swing.exceptionDialog

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
              url = file.toURI.toURL
            } yield url
          }

      println(result.right.map(_.toList))

    } catch {
      case e: Exception ⇒ {
        exceptionDialog(e)
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