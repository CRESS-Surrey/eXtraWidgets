package uk.ac.surrey.xw.note

import org.nlogo.window.GUIWorkspace

import javax.swing.JLabel
import uk.ac.surrey.xw.api.JComponentWidget
import uk.ac.surrey.xw.api.StateUpdater
import uk.ac.surrey.xw.api.StringProperty
import uk.ac.surrey.xw.api.WidgetKey
import uk.ac.surrey.xw.api.annotations.DefaultProperty

@DefaultProperty("TEXT")
class Note(
  val key: WidgetKey,
  val stateUpdater: StateUpdater,
  ws: GUIWorkspace)
  extends JLabel
  with JComponentWidget {

  val xwText = new StringProperty(setText, getText)

}
