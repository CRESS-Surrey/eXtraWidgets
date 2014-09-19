package uk.ac.surrey.soc.cress.extrawidgets.plugin.util

import org.nlogo.app.App
import akka.dispatch.Promise
import uk.ac.surrey.soc.cress.extrawidgets.plugin.util.Swing.enrichComponent
import uk.ac.surrey.soc.cress.extrawidgets.plugin.ExtraWidgetsPlugin
import uk.ac.surrey.soc.cress.extrawidgets.plugin.ExtraWidgetsPlugin
import org.nlogo.app.ToolsMenu
import org.nlogo.app.AppFrame
import javax.swing.JFrame

object NetLogoInitializer {
  import SwingExecutionContext.swingExecutionContext
  val extraWidgetsPlugin = Promise[ExtraWidgetsPlugin]()
  App.main(Array[String]())
  App.app.frame.onComponentShown { e ⇒
    extraWidgetsPlugin.success(
      new ExtraWidgetsPlugin(
        App.app.frame,
        App.app.tabs,
        getToolsMenu(App.app.frame)
      )
    )
  }

  def getToolsMenu(frame: JFrame) = {
    val jMenuBar = frame.getJMenuBar
    (0 until jMenuBar.getMenuCount)
      .map(jMenuBar.getMenu)
      .collect { case m: ToolsMenu ⇒ m }
      .headOption
      .getOrElse(throw new IllegalStateException("Can't find tools menu"))
  }
}
