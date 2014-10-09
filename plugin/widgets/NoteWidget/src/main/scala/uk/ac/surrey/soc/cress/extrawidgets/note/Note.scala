package uk.ac.surrey.soc.cress.extrawidgets.note

import org.nlogo.api.Syntax.StringType
import org.nlogo.window.GUIWorkspace

import javax.swing.JLabel
import uk.ac.surrey.soc.cress.extrawidgets.api.ExtraWidget
import uk.ac.surrey.soc.cress.extrawidgets.api.Kind
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyDef
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyMap
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyValue
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKey

object TextPropertyDef extends PropertyDef[Note] {
  val name = "TEXT"
  def setValueFor(n: Note, newValue: PropertyValue, oldValue: Option[PropertyValue]): Unit =
    n.setText(newValue.toString)
  def unsetValueFor(n: Note) = n.setText("")
}

class NoteKind extends Kind {

  type W = Note
  val name = "NOTE"
  override def propertyDefs = Seq(TextPropertyDef)

  override def newInstance(key: WidgetKey, properties: PropertyMap, ws: GUIWorkspace) = {
    new Note(this, key, properties)
  }

}

class Note(
  val kind: NoteKind,
  val key: WidgetKey,
  properties: PropertyMap)
  extends JLabel
  with ExtraWidget {
  val self = this

  println("Contructing Note " + key + ": " + this.toString)

  // temporary stuff, just to see that it's there
  setOpaque(true)
  setBackground(java.awt.Color.red)
  setBounds(10, 10, 100, 100)
  setText(java.lang.Math.random().toString)

}
