package uk.ac.surrey.soc.cress.extrawidgets.plugin

import java.io.File
import java.io.File.separator
import java.net.JarURLConnection
import java.net.URL
import java.net.URLClassLoader
import java.util.jar.Attributes

import scala.Array.canBuildFrom
import scala.Array.fallbackCanBuildFrom
import scala.Option.option2Iterable

import uk.ac.surrey.soc.cress.extrawidgets.plugin.util.Swing.exceptionDialog
import uk.ac.surrey.soc.cress.extrawidgets.plugin.util.XWException
import uk.ac.surrey.soc.cress.extrawidgets.plugin.util.eitherToRightBiased

object WidgetsLoader {

  val netLogoPluginsFolderName = "plugins"
  val pluginFolderName = ExtraWidgetsPlugin.name
  val widgetsFolderName = "widgets"

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
            val fileURL = file.toURI.toURL
            val parentLoader = getClass.getClassLoader
            val classLoader = URLClassLoader.newInstance(Array(fileURL), parentLoader)
            for {
              attributes ← getManifestAttributes(fileURL).right
              widgetKind ← getAttributeValue(attributes, "Widget-Kind", fileURL).right
              className ← getAttributeValue(attributes, "Class-Name", fileURL).right
              clazz ← loadClass(className, classLoader, fileURL)
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
    println(url)
    val connection = url.openConnection.asInstanceOf[JarURLConnection]
    Option(connection.getManifest())
      .toRight(XWException("Can't find Manifest file in widget jar: " + fileURL + "."))
      .right.map(_.getMainAttributes)
  }

  def getWidgetsFolder: Either[XWException, File] = {
    val pathFromNetLogo =
      netLogoPluginsFolderName + separator + pluginFolderName + separator + widgetsFolderName
    Option(new File(pathFromNetLogo))
      .filter(_.isDirectory())
      .orElse(Some(new File(widgetsFolderName)))
      .filter(_.isDirectory())
      .toRight(XWException("Can't find extra widgets folder."))
  }
}
