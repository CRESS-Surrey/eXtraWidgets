package uk.ac.surrey.soc.cress.extrawidgets.plugin

import java.io.File
import java.io.File.separator

import scala.Array.canBuildFrom

import uk.ac.surrey.soc.cress.extrawidgets.plugin.util.XWException

package object util {

  val netLogoPluginsFolderName = "plugins"
  val pluginFolderName = ExtraWidgetsPlugin.name
  val widgetsFolderName = "widgets"

  def getPluginFolder: Either[XWException, File] = {
    val possibleLocations = Seq(
      netLogoPluginsFolderName + separator + pluginFolderName, // path from NetLogo
      "." // path if we're running the plugin directly (i.e., when testing)
    )
    possibleLocations
      .map(new File(_))
      .filter(_.isDirectory)
      .find(_.listFiles.map(_.getName).contains(ExtraWidgetsPlugin.name + ".jar"))
      .toRight(XWException("Can't find extra widgets plugin folder."))
  }

  def getWidgetsFolder: Either[XWException, File] =
    getPluginFolder.right.flatMap { pluginFolder ⇒
      pluginFolder.listFiles
        .filter(_.isDirectory)
        .find(_.getName == widgetsFolderName)
        .toRight(XWException("Can't find extra widgets folder below plugin folder."))
    }

  implicit def toRunnable[T](block: ⇒ T) =
    new Runnable() { def run() { block } }

}
