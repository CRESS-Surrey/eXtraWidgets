package uk.ac.surrey.soc.cress.extrawidgets

import java.io.File
import java.io.File.separator
import java.net.URLClassLoader
import scala.Array.canBuildFrom
import javax.swing.JOptionPane

package object plugin {

  val name = "eXtraWidgets"
  val jarName = name + ".jar"

  def getPluginJarFile: Either[IllegalStateException, File] =
    getPluginFolder.right.flatMap { pluginFolder ⇒
      pluginFolder.listFiles
        .find(_.getName == jarName)
        .toRight(new IllegalStateException("Can't find plugin JAR file."))
    }

  def getPluginFolder: Either[IllegalStateException, File] = {
    val possibleLocations = Seq(
      "plugins" + separator + name, // path from NetLogo
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

  def exceptionDialog(exception: Exception): Unit = {
    // TODO: the stacktrace should go in the dialog box...
    System.err.println(exception.getMessage + "\n" + exception.getStackTraceString)
    JOptionPane.showMessageDialog(
      null, // parent frame
      exception.getMessage,
      name + " Plugin Error!",
      JOptionPane.ERROR_MESSAGE)
  }
}
