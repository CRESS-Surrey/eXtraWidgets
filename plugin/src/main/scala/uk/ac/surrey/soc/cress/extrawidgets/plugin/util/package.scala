package uk.ac.surrey.soc.cress.extrawidgets.plugin

import java.io.File
import java.io.File.separator
import uk.ac.surrey.soc.cress.extrawidgets.plugin.util.XWException
import java.net.URLClassLoader

package object util {

  val netLogoPluginsFolderName = "plugins"
  val widgetsFolderName = "widgets"
  val pluginFolderName = ExtraWidgetsPlugin.name
  val pluginJarName = ExtraWidgetsPlugin.name + ".jar"

  def getPluginJarFile: Either[XWException, File] =
    getPluginFolder.right.flatMap { pluginFolder ⇒
      pluginFolder.listFiles
        .find(_.getName == pluginJarName)
        .toRight(XWException("Can't find plugin JAR file."))
    }

  def getPluginFolder: Either[XWException, File] = {
    val possibleLocations = Seq(
      netLogoPluginsFolderName + separator + pluginFolderName, // path from NetLogo
      "." // path if we're running the plugin directly (i.e., when testing)
    )
    possibleLocations
      .map(new File(_))
      .filter(_.isDirectory)
      .find(_.listFiles.map(_.getName).contains(pluginJarName))
      .toRight(XWException("Can't find extra widgets plugin folder."))
  }

  def getWidgetsFolder: Either[XWException, File] =
    getPluginFolder.right.flatMap { pluginFolder ⇒
      pluginFolder.listFiles
        .filter(_.isDirectory)
        .find(_.getName == widgetsFolderName)
        .toRight(XWException("Can't find extra widgets folder below plugin folder."))
    }

  def newClassLoader(jarFile: File): URLClassLoader = {
    val jarURLs = addCompanionJars(jarFile).map(_.toURI.toURL)
    val parentLoader = getClass.getClassLoader
    URLClassLoader.newInstance(jarURLs, parentLoader)
  }

  def addCompanionJars(jarFile: File): Array[File] =
    jarFile.getAbsoluteFile.getParentFile.listFiles
      .filter(_.getName.toUpperCase.endsWith(".JAR"))

  implicit def toRunnable[T](block: ⇒ T) =
    new Runnable() { def run() { block } }

}
