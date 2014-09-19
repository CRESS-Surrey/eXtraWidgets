package uk.ac.surrey.soc.cress.extrawidgets.plugin.util

import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent

import org.nlogo.app.App

import akka.dispatch.Promise

object NetLogoInitializer {
  import SwingExecutionContext.swingExecutionContext
  val app = Promise[App]()
  App.main(Array[String]())
  App.app.frame.addComponentListener(new ComponentAdapter() {
    override def componentShown(e: ComponentEvent) {
      app.success(App.app)
    }
  })
}
