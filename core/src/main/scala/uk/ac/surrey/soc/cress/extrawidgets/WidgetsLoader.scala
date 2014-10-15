package uk.ac.surrey.soc.cress.extrawidgets

import java.io.File
import java.io.File.separator
import java.net.JarURLConnection
import java.net.URL
import java.net.URLClassLoader
import java.util.jar.Attributes
import java.util.jar.JarFile

import scala.Array.canBuildFrom
import scala.collection.JavaConverters.enumerationAsScalaIteratorConverter

import uk.ac.surrey.soc.cress.extrawidgets.api.ExtraWidget
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKind
import uk.ac.surrey.soc.cress.extrawidgets.api.XWException
import uk.ac.surrey.soc.cress.extrawidgets.gui.Tab

object WidgetsLoader {

  def loadWidgetKinds(): Map[String, WidgetKind] = {
    val widgetKinds =
      for {
        folder ← getWidgetsFolder.listFiles
        if folder.isDirectory
        file ← folder.listFiles
        if file.getName.toUpperCase == (folder.getName + ".jar").toUpperCase
        classLoader = newClassLoader(file, getClass.getClassLoader)
        className ← classNamesIn(file)
        clazz = loadClass(className, classLoader, file.toURI.toURL)
        if classOf[ExtraWidget].isAssignableFrom(clazz)
        widgetClass = clazz.asSubclass(classOf[ExtraWidget])
      } yield new WidgetKind(widgetClass)
    (new WidgetKind(classOf[Tab]) +: widgetKinds)
      .map(kind ⇒ kind.name -> kind)(collection.breakOut)
  }

  def classNamesIn(jar: File): Iterator[String] =
    for {
      entry ← new JarFile(jar).entries.asScala
      entryName = entry.getName
      if entryName.endsWith(".class")
      className = entryName
        .stripSuffix(".class")
        .replaceAllLiterally("/", ".")
    } yield className

  def getAttributeValue(attributes: Attributes, attributeName: String, fileURL: URL): Either[XWException, String] =
    Option(attributes.getValue(attributeName))
      .toRight(XWException("Bad widget: Can't find attribute " +
        attributeName + " class name in Manifest for " + fileURL + "."))

  def loadClass(
    className: String,
    classLoader: ClassLoader,
    fileURL: URL): Class[_] =
    try classLoader.loadClass(className)
    catch {
      case e: ClassNotFoundException ⇒
        throw new XWException("Can't find class " + className +
          "\n in widget jar: " + fileURL + ".", e)
      case e: NoClassDefFoundError ⇒
        throw new XWException("No class definition found for " + className +
          "\n in widget jar: " + fileURL + ".")
    }

  def getManifestAttributes(fileURL: URL): Either[XWException, Attributes] = {
    val url = new URL("jar", "", fileURL + "!/")
    val connection = url.openConnection.asInstanceOf[JarURLConnection]
    Option(connection.getManifest())
      .toRight(XWException("Can't find Manifest file in widget jar: " + fileURL + "."))
      .right.map(_.getMainAttributes)
  }

  def getWidgetsFolder: File =
    getXWFolder
      .listFiles
      .filter(_.isDirectory)
      .find(_.getName == "widgets")
      .getOrElse(throw new XWException("Can't find extra widgets folder below extension folder."))

  def getXWFolder: File = {
    val possibleLocations = Seq(
      "extensions" + separator + "xw", // path from NetLogo
      "../xw" // path if we're running from the core tests
    )
    possibleLocations
      .map(new File(_).getCanonicalFile)
      .filter(_.isDirectory)
      .find(_.listFiles.map(_.getName).contains("xw.jar"))
      .getOrElse(throw new XWException("Can't find \"xw\" extension folder."))
  }

  def newClassLoader(jarFile: File, parentLoader: ClassLoader): URLClassLoader = {
    val jarURLs = addCompanionJars(jarFile).map(_.toURI.toURL)
    URLClassLoader.newInstance(jarURLs, parentLoader)
  }

  def addCompanionJars(jarFile: File): Array[File] =
    jarFile.getAbsoluteFile.getParentFile.listFiles
      .filter(_.getName.toUpperCase.endsWith(".JAR"))

}
