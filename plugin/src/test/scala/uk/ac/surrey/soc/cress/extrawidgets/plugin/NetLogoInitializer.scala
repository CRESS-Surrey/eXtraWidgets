package uk.ac.surrey.soc.cress.extrawidgets.plugin

import org.nlogo.app.App
import org.nlogo.app.ToolsMenu

import akka.dispatch.Await
import akka.dispatch.Promise
import akka.util.duration.intToDurationInt
import javax.swing.JFrame
import uk.ac.surrey.soc.cress.extrawidgets.plugin.util.Swing.enrichComponent

object NetLogoInitializer {

  import SwingExecutionContext.swingExecutionContext

  private val ewpPromise = Promise[ExtraWidgetsPlugin]()
  App.main(Array[String]())
  App.app.frame.onComponentShown { e ⇒
    ewpPromise.success(
      new ExtraWidgetsPlugin(App.app, getToolsMenu(App.app.frame))
    )
  }

  def extraWidgetsPlugin = Await.result(ewpPromise, 30 seconds)

  def getToolsMenu(frame: JFrame) = {
    val jMenuBar = frame.getJMenuBar
    (0 until jMenuBar.getMenuCount)
      .map(jMenuBar.getMenu)
      .collect { case m: ToolsMenu ⇒ m }
      .headOption
      .getOrElse(throw new Exception("Can't find tools menu."))
  }
}
