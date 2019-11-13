package uk.ac.surrey.soc.cress.extrawidgets

import javax.swing.JOptionPane

package object gui {
  def exceptionDialog(exception: Exception): Unit = {
    // TODO: the stacktrace should go in the dialog box...
    System.err.println(exception.getMessage + "\n" + exception.getStackTrace.mkString("\n"))
    JOptionPane.showMessageDialog(
      null, // parent frame
      exception.getMessage,
      "eXtraWidgets Plugin Error!",
      JOptionPane.ERROR_MESSAGE)
  }
}
