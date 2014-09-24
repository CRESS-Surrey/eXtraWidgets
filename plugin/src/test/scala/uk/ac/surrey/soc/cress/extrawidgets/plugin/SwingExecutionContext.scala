package uk.ac.surrey.soc.cress.extrawidgets.plugin

import java.util.concurrent.Executor
import akka.dispatch.ExecutionContext
import javax.swing.SwingUtilities

object SwingExecutionContext {
  implicit val swingExecutionContext: ExecutionContext = ExecutionContext.fromExecutor(new Executor {
    def execute(command: Runnable): Unit = SwingUtilities invokeLater command
  })
}
