package uk.ac.surrey.soc.cress.extrawidgets.gui

import org.nlogo.app.App
import org.nlogo.app.ToolsMenu

import akka.dispatch.Await
import akka.dispatch.Promise
import akka.util.duration.intToDurationInt
import javax.swing.JFrame
import uk.ac.surrey.soc.cress.extrawidgets.gui.Swing.enrichComponent
import uk.ac.surrey.soc.cress.extrawidgets.plugin.ExtraWidgetsPlugin

object NetLogoInitializer {

import SwingExecutionContext.swingExecutionContext

  private val ewpPromise = Promise[ExtraWidgetsPlugin]()
  App.main(Array[String]())
  App.app.frame.onComponentShown { e ⇒
    ewpPromise.success(
      new ExtraWidgetsPlugin(App.app, getToolsMenu(App.app.frame))
    )
  }

  def extraWidgetsManager =
    Await.result(ewpPromise, 30 seconds)
      .manager.right.get.asInstanceOf[Manager]

  def getToolsMenu(frame: JFrame) = {
    val jMenuBar = frame.getJMenuBar
    (0 until jMenuBar.getMenuCount)
      .map(jMenuBar.getMenu)
      .collect { case m: ToolsMenu ⇒ m }
      .headOption
      .getOrElse(throw new Exception("Can't find tools menu."))
  }
}
