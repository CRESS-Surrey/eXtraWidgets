package uk.ac.surrey.soc.cress.extrawidgets.plugin.controller

import uk.ac.surrey.soc.cress.extrawidgets.plugin.model.Writer
import uk.ac.surrey.soc.cress.extrawidgets.plugin.view.View

class Controller(writer: Writer) {
  def addTab(id: String): Either[String, Unit] = {
    writer.add("tab", id)
  }
}