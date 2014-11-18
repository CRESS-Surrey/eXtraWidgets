package uk.ac.surrey.xw.gui

import org.nlogo.app.App
import org.nlogo.app.ToolsMenu
import org.nlogo.window.GUIWorkspace
import akka.dispatch.Await
import akka.dispatch.Promise
import akka.util.duration.intToDurationInt
import uk.ac.surrey.xw.gui.Swing.enrichComponent;

import java.io.File

object NetLogoInitializer {

  import SwingExecutionContext.swingExecutionContext

  App.main(Array[String]())
  val wsPromise = Promise[GUIWorkspace]()
  App.app.frame.onComponentShown { _ ⇒
    wsPromise.success(App.app.workspace)
  }

  val ws = Await.result(wsPromise, 10 seconds)
  val frame = App.app.frame
  val xwPath = new File("../xw/xw.jar").getCanonicalPath
  ws.getExtensionManager.importExtension(xwPath, null)

}
