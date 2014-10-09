package uk.ac.surrey.soc.cress.extrawidgets.plugin

import java.io.File
import java.io.File.separator
import java.net.URLClassLoader

import scala.Array.canBuildFrom

/**
 * This module is used by both the Plugin subproject and the Common subproject,
 * but since neither of those can depend on the other (and that Plugin cannot
 * have external dependencies at all) there is a copy of this file in both project.
 * Both copies should be identical, apart from the package declaration.
 * This situation is far from ideal (though accidental divergence would not be that
 * much of a problem), but I currently have no idea how to resolve it. NP 2014-10-09.
 */
object LoaderUtil {

  val pluginName = "eXtraWidgets"
  val jarName = pluginName + ".jar"

  def getWidgetsFolder: Either[IllegalStateException, File] =
    getPluginFolder.right.flatMap { pluginFolder ⇒
      pluginFolder.listFiles
        .filter(_.isDirectory)
        .find(_.getName == "widgets")
        .toRight(new IllegalStateException("Can't find extra widgets folder below plugin folder."))
    }

  def getPluginFolder: Either[IllegalStateException, File] = {
    val possibleLocations = Seq(
      "plugins" + separator + "eXtraWidgets", // path from NetLogo
      ".." + separator + "plugin" // path if we're running from the tests
    )
    possibleLocations
      .map(new File(_).getCanonicalFile)
      .filter(_.isDirectory)
      .find(_.listFiles.map(_.getName).contains(jarName))
      .toRight(new IllegalStateException("Can't find extra widgets plugin folder."))
  }

  def newClassLoader(jarFile: File, parentLoader: ClassLoader): URLClassLoader = {
    val jarURLs = addCompanionJars(jarFile).map(_.toURI.toURL)
    URLClassLoader.newInstance(jarURLs, parentLoader)
  }

  def addCompanionJars(jarFile: File): Array[File] =
    jarFile.getAbsoluteFile.getParentFile.listFiles
      .filter(_.getName.toUpperCase.endsWith(".JAR"))
}
