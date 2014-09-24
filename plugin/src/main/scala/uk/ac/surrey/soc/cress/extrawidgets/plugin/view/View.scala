package uk.ac.surrey.soc.cress.extrawidgets.plugin.view

import uk.ac.surrey.soc.cress.extrawidgets.plugin.gui.GUI
import uk.ac.surrey.soc.cress.extrawidgets.plugin.model.Reader

class View(reader: Reader, gui: GUI) {

  def refresh(): Unit = {

    val guiWidgets = gui.makeWidgetsMap

    val (keysOfExistingWidgets, keysOfMissingWidgets) =
      reader.widgetMap.keys.partition(guiWidgets.contains)

    keysOfMissingWidgets.flatMap(reader.widgetMap.get).foreach(gui.createWidget)
    keysOfExistingWidgets.flatMap(reader.widgetMap.get).foreach(gui.updateWidget)

    val deadWidgets = guiWidgets.filterKeys(reader.widgetMap.contains).values
    deadWidgets.foreach(gui.removeWidget)
  }

}