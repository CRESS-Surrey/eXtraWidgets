package uk.ac.surrey.soc.cress.extrawidgets.plugin

import java.io.File
import java.net.JarURLConnection
import java.net.URL
import java.net.URLClassLoader
import java.util.jar.Attributes

import scala.Array.canBuildFrom
import scala.Array.fallbackCanBuildFrom
import scala.Option.option2Iterable

import uk.ac.surrey.soc.cress.extrawidgets.plugin.util.Swing.exceptionDialog
import uk.ac.surrey.soc.cress.extrawidgets.plugin.util.XWException
import uk.ac.surrey.soc.cress.extrawidgets.plugin.util.getWidgetsFolder

object WidgetsLoader {

  def loadExtraWidgets(): Map[String, Class[_]] = {

    getWidgetsFolder.right.map {
      case widgetsFolder ⇒

        val widgetJarFiles = for {
          folder ← widgetsFolder.listFiles
          if folder.isDirectory
          file ← folder.listFiles
          if file.getName.toUpperCase == (folder.getName + ".jar").toUpperCase
        } yield file

        val entries: Seq[Either[XWException, (String, java.lang.Class[_])]] =
          widgetJarFiles.map { file ⇒
            val widgetJarURL = file.toURI.toURL
            val allJarURLs = getJarURLs(file)
            val parentLoader = getClass.getClassLoader
            val classLoader = URLClassLoader.newInstance(allJarURLs, parentLoader)
            for {
              attributes ← getManifestAttributes(widgetJarURL).right
              widgetKind ← getAttributeValue(attributes, "Widget-Kind", widgetJarURL).right
              className ← getAttributeValue(attributes, "Class-Name", widgetJarURL).right
              clazz ← loadClass(className, classLoader, widgetJarURL).right
            } yield (widgetKind, clazz)
          }

        entries.flatMap {
          _.fold(e ⇒ { exceptionDialog(e); None }, Some(_))
        }(collection.breakOut): Map[String, Class[_]]
    }.fold(e ⇒ { exceptionDialog(e); Map.empty }, identity)
  }

  def getJarURLs(widgetJarFile: File): Array[URL] =
    widgetJarFile.getAbsoluteFile.getParentFile.listFiles
      .filter(_.getName.toUpperCase.endsWith(".JAR"))
      .map(_.toURI.toURL)

  def getAttributeValue(attributes: Attributes, attributeName: String, fileURL: URL): Either[XWException, String] =
    Option(attributes.getValue(attributeName))
      .toRight(XWException("Bad widget: Can't find attribute " +
        attributeName + " class name in Manifest for " + fileURL + "."))

  def loadClass(className: String, classLoader: ClassLoader, fileURL: URL): Either[XWException, Class[_]] =
    try Right(classLoader.loadClass(className))
    catch {
      case e: ClassNotFoundException ⇒
        Left(XWException("Can't find class " + className +
          "\n in widget jar: " + fileURL + ".", e))
      case e: NoClassDefFoundError ⇒
        Left(XWException("No class definition found for " + className +
          "\n in widget jar: " + fileURL + ".", e))
    }

  def getManifestAttributes(fileURL: URL): Either[XWException, Attributes] = {
    val url = new URL("jar", "", fileURL + "!/")
    val connection = url.openConnection.asInstanceOf[JarURLConnection]
    Option(connection.getManifest())
      .toRight(XWException("Can't find Manifest file in widget jar: " + fileURL + "."))
      .right.map(_.getMainAttributes)
  }

}
