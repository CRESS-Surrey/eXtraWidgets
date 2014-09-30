package uk.ac.surrey.soc.cress.extrawidgets.plugin.controller

import uk.ac.surrey.soc.cress.extrawidgets.state.Writer

class Controller(writer: Writer) {
  def addTab(id: String): Either[String, Unit] = {
    writer.add("tab", id)
  }
  def removeTab(id: String): Unit = {
    writer.remove(id)
  }
}
