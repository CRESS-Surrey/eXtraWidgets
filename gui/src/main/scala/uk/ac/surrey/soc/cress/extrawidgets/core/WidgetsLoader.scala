

package uk.ac.surrey.soc.cress.extrawidgets.core

import java.io.File
import java.net.JarURLConnection
import java.net.URL
import java.util.jar.Attributes

import scala.Array.canBuildFrom
import scala.Array.fallbackCanBuildFrom

import uk.ac.surrey.soc.cress.extrawidgets.api.Kind
import uk.ac.surrey.soc.cress.extrawidgets.api.XWException
import uk.ac.surrey.soc.cress.extrawidgets.plugin.exceptionDialog
import uk.ac.surrey.soc.cress.extrawidgets.plugin.getPluginFolder
import uk.ac.surrey.soc.cress.extrawidgets.plugin.newClassLoader

object WidgetsLoader {

  def loadWidgetKinds(): Map[String, Kind] = {

    getWidgetsFolder.right.map {
      case widgetsFolder ⇒

        val widgetJarFiles = for {
          folder ← widgetsFolder.listFiles
          if folder.isDirectory
          file ← folder.listFiles
          if file.getName.toUpperCase == (folder.getName + ".jar").toUpperCase
        } yield file

        val entries: Seq[Either[XWException, Kind]] =
          widgetJarFiles.map { widgetJarFile ⇒
            val widgetJarURL = widgetJarFile.toURI.toURL
            val classLoader = newClassLoader(widgetJarFile, getClass.getClassLoader)
            for {
              attributes ← getManifestAttributes(widgetJarURL).right
              className ← getAttributeValue(attributes, "Class-Name", widgetJarURL).right
              clazz ← loadClass(className, classLoader, widgetJarURL).right
              kind ← getKind(clazz).right
            } yield kind
          }

        entries.collect {
          case Left(e) ⇒ exceptionDialog(e)
        }

        val kindMap = entries.collect {
          case Right(kind) ⇒ kind.name -> kind
        }(collection.breakOut): Map[String, Kind]

        kindMap + ("TAB" -> new TabKind)

    }.fold(e ⇒ { exceptionDialog(e); Map.empty }, identity)
  }

  def getKind(clazz: Class[_]): Either[XWException, Kind] = {
    try Right(clazz.newInstance.asInstanceOf[Kind])
    catch {
      case e: ClassCastException ⇒ Left(XWException(
        clazz.getName + " does not implement WidgetKind trait."))
    }
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
