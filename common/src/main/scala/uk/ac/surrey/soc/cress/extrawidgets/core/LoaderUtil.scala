package uk.ac.surrey.soc.cress.extrawidgets.core

import java.io.File
import java.io.File.separator
import java.net.URLClassLoader

import scala.Array.canBuildFrom

import uk.ac.surrey.soc.cress.extrawidgets.api.XWException

object LoaderUtil {

  val jarName = "xw.jar"

  def getWidgetsFolder: Either[XWException, File] =
    getXWFolder.right.flatMap { xwFolder ⇒
      xwFolder.listFiles
        .filter(_.isDirectory)
        .find(_.getName == "widgets")
        .toRight(new XWException("Can't find extra widgets folder below extension folder."))
    }

  def getXWFolder: Either[XWException, File] = {
    val possibleLocations = Seq(
      "extensions" + separator + "xw", // path from NetLogo
      ".." + separator + "extension" // path if we're running from the tests
    )
    possibleLocations
      .map(new File(_).getCanonicalFile)
      .filter(_.isDirectory)
      .find(_.listFiles.map(_.getName).contains(jarName))
      .toRight(new XWException("Can't find \"xw\" extension folder."))
  }

  def newClassLoader(jarFile: File, parentLoader: ClassLoader): URLClassLoader = {
    val jarURLs = addCompanionJars(jarFile).map(_.toURI.toURL)
    URLClassLoader.newInstance(jarURLs, parentLoader)
  }

  def addCompanionJars(jarFile: File): Array[File] =
    jarFile.getAbsoluteFile.getParentFile.listFiles
      .filter(_.getName.toUpperCase.endsWith(".JAR"))
}
