package uk.ac.surrey.soc.cress.extrawidgets

import java.io.File
import java.io.File.separator
import java.net.URLClassLoader
import scala.Array.canBuildFrom
import javax.swing.JOptionPane

package object plugin {

  def exceptionDialog(exception: Exception): Unit = {
    // TODO: the stacktrace should go in the dialog box...
    System.err.println(exception.getMessage + "\n" + exception.getStackTraceString)
    JOptionPane.showMessageDialog(
      null, // parent frame
      exception.getMessage,
      "eXtraWidgets Plugin Error!",
      JOptionPane.ERROR_MESSAGE)
  }
}
