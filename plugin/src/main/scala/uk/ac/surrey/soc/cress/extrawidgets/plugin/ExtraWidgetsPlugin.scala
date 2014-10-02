package uk.ac.surrey.soc.cress.extrawidgets.plugin

import org.nlogo.app.App
import org.nlogo.app.ToolsMenu

import javax.swing.JPanel

class ExtraWidgetsPlugin(val app: App, val toolsMenu: ToolsMenu) extends JPanel {

  val manager = getPluginJarFile.right.map { jarFile ⇒
    val classLoader = newClassLoader(jarFile, getClass.getClassLoader)
    classLoader
      .loadClass("uk.ac.surrey.soc.cress.extrawidgets.core.Manager")
      .getConstructor(classOf[App], classOf[ToolsMenu])
      .newInstance(app, toolsMenu)
  }

  manager.left.foreach(exceptionDialog)

}
