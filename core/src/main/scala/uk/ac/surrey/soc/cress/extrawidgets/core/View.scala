package uk.ac.surrey.soc.cress.extrawidgets.core

import java.awt.EventQueue.isDispatchThread

import org.nlogo.awt.EventQueue.invokeAndWait

import uk.ac.surrey.soc.cress.extrawidgets.api.util.toRunnable
import uk.ac.surrey.soc.cress.extrawidgets.state.Reader

class View(reader: Reader, gui: GUI) {

  reader.onChange {

    // make sure we are on the AWT event thread, because the change could
    // have been triggered from an extension running in the job thread:
    if (isDispatchThread()) refresh()
    else invokeAndWait { refresh() }

    def refresh() {
      println("*refresh*")

      val guiWidgets = gui.makeWidgetsMap

      val (keysOfExistingWidgets, keysOfMissingWidgets) =
        reader.widgetKeySet.partition(guiWidgets.contains)

      for {
        key ← keysOfMissingWidgets
        propertyMap ← reader.propertyMap(key)
      } gui.createWidget(key, propertyMap)

      for {
        key ← keysOfExistingWidgets
        propertyMap ← reader.propertyMap(key)
      } gui.updateWidget(key, propertyMap)

      val deadWidgets = guiWidgets.filterKeys(key ⇒ !reader.contains(key)).values
      deadWidgets.foreach(gui.removeWidget)
    }
  }
}
