package uk.ac.surrey.soc.cress.extrawidgets.note

import org.nlogo.window.GUIWorkspace
import javax.swing.JLabel
import uk.ac.surrey.soc.cress.extrawidgets.api.ExtraWidget
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyMap
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKey
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyDef

class TextProperty(note: Note) extends PropertyDef(note) {
  type ValueType = String
  override def setValue(newValue: String, oldValue: Option[String]): Unit =
    note.setText(newValue)
  override def getValue = note.getText
  override def defaultValue = ""
}

class Note(val key: WidgetKey, properties: PropertyMap, ws: GUIWorkspace)
  extends JLabel with ExtraWidget {

  val text = new TextProperty(this)

  // temporary stuff, just to see that it's there
  setOpaque(true)
  setBackground(java.awt.Color.red)
  setBounds(10, 10, 100, 100)
  setText(java.lang.Math.random().toString)

}
