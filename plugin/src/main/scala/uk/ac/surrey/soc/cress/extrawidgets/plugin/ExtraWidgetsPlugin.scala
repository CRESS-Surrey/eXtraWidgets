package uk.ac.surrey.soc.cress.extrawidgets.plugin

import java.io.File

import org.nlogo.app.App
import org.nlogo.app.ToolsMenu

import LoaderUtil.getPluginFolder
import LoaderUtil.jarName
import LoaderUtil.newClassLoader
import javax.swing.JPanel

class ExtraWidgetsPlugin(val app: App, val toolsMenu: ToolsMenu) extends JPanel {

  val manager = getPluginJarFile.right.map { jarFile ⇒
    val classLoader = newClassLoader(jarFile, getClass.getClassLoader)
    classLoader
      .loadClass("uk.ac.surrey.soc.cress.extrawidgets.gui.Manager")
      .getConstructor(classOf[App], classOf[ToolsMenu])
      .newInstance(app, toolsMenu)
  }

  manager.left.foreach(exceptionDialog)

  def getPluginJarFile: Either[IllegalStateException, File] =
    getPluginFolder.right.flatMap { pluginFolder ⇒
      pluginFolder.listFiles
        .find(_.getName == jarName)
        .toRight(new IllegalStateException("Can't find plugin JAR file."))
    }
}
