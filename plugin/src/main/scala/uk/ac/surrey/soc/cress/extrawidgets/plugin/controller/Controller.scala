package uk.ac.surrey.soc.cress.extrawidgets.plugin.controller

import uk.ac.surrey.soc.cress.extrawidgets.plugin.model.Writer

class Controller(writer: Writer) {
  def addTab(id: String): Either[String, Unit] = writer.add("tab", id)
}