package uk.ac.surrey.soc.cress.extrawidgets.core

import java.net.JarURLConnection
import java.net.URL
import java.util.jar.Attributes

import scala.Array.canBuildFrom
import scala.Array.fallbackCanBuildFrom

import LoaderUtil.getWidgetsFolder
import LoaderUtil.newClassLoader
import uk.ac.surrey.soc.cress.extrawidgets.api.ExtraWidget
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKind
import uk.ac.surrey.soc.cress.extrawidgets.api.XWException
import uk.ac.surrey.soc.cress.extrawidgets.api.tryTo
import uk.ac.surrey.soc.cress.extrawidgets.gui.Tab

object WidgetsLoader {

  def loadWidgetKinds(): Either[Seq[Exception], Map[String, WidgetKind]] = {

    getWidgetsFolder.right.map {
      case widgetsFolder ⇒

        val widgetJarFiles = for {
          folder ← widgetsFolder.listFiles
          if folder.isDirectory
          file ← folder.listFiles
          if file.getName.toUpperCase == (folder.getName + ".jar").toUpperCase
        } yield file

        val entries: Seq[Either[XWException, WidgetKind]] =
          getKind(classOf[Tab]) +: widgetJarFiles.map { widgetJarFile ⇒
            val widgetJarURL = widgetJarFile.toURI.toURL
            val classLoader = newClassLoader(widgetJarFile, getClass.getClassLoader)
            for {
              attributes ← getManifestAttributes(widgetJarURL).right
              className ← getAttributeValue(attributes, "Class-Name", widgetJarURL).right
              clazz ← loadClass(className, classLoader, widgetJarURL).right
              kind ← getKind(clazz).right
            } yield kind
          }

        val exceptions = entries.collect { case Left(e) ⇒ e }

        if (exceptions.nonEmpty)
          Left(exceptions)
        else
          Right(entries.collect {
            case Right(kind) ⇒ kind.name -> kind
          }(collection.breakOut): Map[String, WidgetKind])

    }.left.map(Seq(_)).joinRight
  }

  def getKind(clazz: Class[_ <: ExtraWidget]): Either[XWException, WidgetKind] =
    tryTo(
      new WidgetKind(clazz),
      "Unable to instantiate Kind object for " + clazz.getName + ".")

  def getAttributeValue(attributes: Attributes, attributeName: String, fileURL: URL): Either[XWException, String] =
    Option(attributes.getValue(attributeName))
      .toRight(XWException("Bad widget: Can't find attribute " +
        attributeName + " class name in Manifest for " + fileURL + "."))

  def loadClass(
    className: String,
    classLoader: ClassLoader,
    fileURL: URL): Either[XWException, Class[_ <: ExtraWidget]] =
    try Right(classLoader.loadClass(className).asSubclass(classOf[ExtraWidget]))
    catch {
      case e: ClassCastException ⇒
        Left(XWException("Class " + className +
          "\n in widget jar: " + fileURL +
          " is not an implementation of ExtraWidget.", e))
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
