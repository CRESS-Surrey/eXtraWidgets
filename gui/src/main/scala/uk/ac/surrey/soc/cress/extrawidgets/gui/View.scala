package uk.ac.surrey.soc.cress.extrawidgets.gui

import java.awt.EventQueue.isDispatchThread

import org.nlogo.awt.EventQueue.invokeLater

import uk.ac.surrey.soc.cress.extrawidgets.api.toRunnable
import uk.ac.surrey.soc.cress.extrawidgets.state.Reader

class View(reader: Reader, gui: GUI) {

  reader.onChange {

    // make sure we are on the AWT event thread, because the change could
    // have been triggered from an extension running in the job thread:
    if (isDispatchThread()) { println("*refresh from dispatch*"); refresh() }
    else invokeLater { println("*refresh from job*"); refresh() }

    def refresh() {

      val guiWidgets = gui.makeWidgetsMap

      val (keysOfExistingWidgets, keysOfMissingWidgets) =
        reader.widgetKeySet.partition(guiWidgets.contains)

      for {
        key ← keysOfMissingWidgets
        propertyMap ← reader.propertyMap(key).right
      } gui.createWidget(key, propertyMap)

      for {
        key ← keysOfExistingWidgets
        propertyMap ← reader.propertyMap(key).right
        guiWidget ← guiWidgets.get(key)
      } gui.updateWidget(guiWidget, propertyMap)

      val deadWidgets = guiWidgets.filterKeys(key ⇒ !reader.contains(key)).values
      deadWidgets.foreach(gui.removeWidget)

    }
  }
}
