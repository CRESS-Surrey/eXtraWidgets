package uk.ac.surrey.xw.note

import java.awt.Color.white

import javax.swing.JLabel

import org.nlogo.window.GUIWorkspace

import uk.ac.surrey.xw.api.BooleanProperty
import uk.ac.surrey.xw.api.ColorProperty
import uk.ac.surrey.xw.api.JComponentWidget
import uk.ac.surrey.xw.api.JComponentWidgetKind
import uk.ac.surrey.xw.api.State
import uk.ac.surrey.xw.api.StringProperty
import uk.ac.surrey.xw.api.WidgetKey

class NoteKind[W <: Note] extends JComponentWidgetKind[W] {
  override val name = "NOTE"
  override val newWidget = new Note(_, _, _)
  override val colorProperty = new ColorProperty[W](
    "COLOR", Some(_.setBackground(_)), _.getBackground, white)
  override val opaqueProperty = new BooleanProperty[W](
    "OPAQUE", Some((w, b) ⇒ { w.setOpaque(b); w.updateBorder() }), _.isOpaque, false)
  val textProperty = new StringProperty[W]("TEXT", Some(_.setText(_)), _.getText)
  val defaultProperty = Some(textProperty)
  override def propertySet = super.propertySet + textProperty
}

class Note(
  val key: WidgetKey,
  val state: State,
  val ws: GUIWorkspace)
  extends JLabel
  with JComponentWidget {
  val kind = new NoteKind[this.type]
}
