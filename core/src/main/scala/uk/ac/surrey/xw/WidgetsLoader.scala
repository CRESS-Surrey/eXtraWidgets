package uk.ac.surrey.xw

import java.io.File
import java.lang.reflect.Modifier.isAbstract
import java.lang.reflect.Modifier.isPublic
import java.net.JarURLConnection
import java.net.URL
import java.net.URLClassLoader
import java.util.jar.Attributes
import java.util.jar.JarFile

import scala.collection.JavaConverters.enumerationAsScalaIteratorConverter

import uk.ac.surrey.xw.api.ExtraWidget
import uk.ac.surrey.xw.api.TabKind
import uk.ac.surrey.xw.api.WidgetKind
import uk.ac.surrey.xw.api.XWException

object WidgetsLoader {

  def loadWidgetKinds(extensionFolder: File): Map[String, WidgetKind[_]] = {
    val widgetKinds =
      for {
        folder ← getWidgetsFolder(extensionFolder).listFiles
        if folder.isDirectory
        file ← folder.listFiles
        if file.getName.toUpperCase == (folder.getName + ".jar").toUpperCase
        classLoader = newClassLoader(file, getClass.getClassLoader)
        className ← classNamesIn(file)
        clazz = loadClass(className, classLoader, file.toURI.toURL)
        modifiers = clazz.getModifiers
        if isPublic(modifiers) && !isAbstract(modifiers) &&
          classOf[WidgetKind[_]].isAssignableFrom(clazz)
      } yield {
        clazz
          .getDeclaredConstructor(Seq.empty[Class[_]] : _*)
          .newInstance()
          .asInstanceOf[WidgetKind[_ <: ExtraWidget]]
      }
    (new TabKind +: widgetKinds)
      .map(kind ⇒ kind.name -> kind)
      .toMap
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

  def getWidgetsFolder(extensionFolder: File): File =
    extensionFolder
      .listFiles
      .filter(_.isDirectory)
      .find(_.getName == "widgets")
      .getOrElse(throw new XWException("Can't find extra widgets folder below extension folder."))

  def newClassLoader(jarFile: File, parentLoader: ClassLoader): URLClassLoader = {
    val jarURLs = addCompanionJars(jarFile).map(_.toURI.toURL)
    URLClassLoader.newInstance(jarURLs, parentLoader)
  }

  def addCompanionJars(jarFile: File): Array[File] =
    jarFile.getAbsoluteFile.getParentFile.listFiles
      .filter(_.getName.toUpperCase.endsWith(".JAR"))

}
