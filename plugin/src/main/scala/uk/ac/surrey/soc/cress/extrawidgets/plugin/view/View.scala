package uk.ac.surrey.soc.cress.extrawidgets.plugin.view

import org.nlogo.awt.EventQueue.invokeLater

import uk.ac.surrey.soc.cress.extrawidgets.plugin.gui.GUI
import uk.ac.surrey.soc.cress.extrawidgets.plugin.model.Reader
import uk.ac.surrey.soc.cress.extrawidgets.plugin.util.toRunnable

class View(reader: Reader, gui: GUI) {

  reader.onChange {
    // make sure we are on the AWT event thread, because the change could
    // have been triggered from an extension running in the job thread:
    invokeLater {

      println("*refresh*")

      val guiWidgets = gui.makeWidgetsMap

      val (keysOfExistingWidgets, keysOfMissingWidgets) =
        reader.widgetMap.keys.partition(guiWidgets.contains)

      for {
        id ← keysOfMissingWidgets
        propertyMap ← reader.widgetMap.get(id)
      } gui.createWidget(id, propertyMap)

      for {
        id ← keysOfExistingWidgets
        propertyMap ← reader.widgetMap.get(id)
      } gui.updateWidget(id, propertyMap)

      val deadWidgets = guiWidgets.filterKeys(id ⇒ !reader.widgetMap.contains(id)).values
      deadWidgets.foreach(gui.removeWidget)
    }
  }

}