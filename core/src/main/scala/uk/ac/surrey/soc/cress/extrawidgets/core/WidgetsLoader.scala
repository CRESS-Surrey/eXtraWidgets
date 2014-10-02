

package uk.ac.surrey.soc.cress.extrawidgets.core

import java.io.File
import java.net.JarURLConnection
import java.net.URL
import java.util.jar.Attributes

import scala.Array.canBuildFrom
import scala.Array.fallbackCanBuildFrom
import scala.Option.option2Iterable

import uk.ac.surrey.soc.cress.extrawidgets.api.XWException
import uk.ac.surrey.soc.cress.extrawidgets.plugin.getPluginFolder
import uk.ac.surrey.soc.cress.extrawidgets.plugin.newClassLoader
import uk.ac.surrey.soc.cress.extrawidgets.plugin.exceptionDialog

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
          widgetJarFiles.map { widgetJarFile ⇒
            val widgetJarURL = widgetJarFile.toURI.toURL
            val classLoader = newClassLoader(widgetJarFile, getClass.getClassLoader)
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

  def getWidgetsFolder: Either[IllegalStateException, File] =
    getPluginFolder.right.flatMap { pluginFolder ⇒
      pluginFolder.listFiles
        .filter(_.isDirectory)
        .find(_.getName == "widgets")
        .toRight(new IllegalStateException("Can't find extra widgets folder below plugin folder."))
    }


}
