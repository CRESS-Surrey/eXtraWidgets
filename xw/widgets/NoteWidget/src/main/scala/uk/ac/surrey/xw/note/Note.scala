package uk.ac.surrey.xw.note

import org.nlogo.window.GUIWorkspace

import javax.swing.JLabel
import uk.ac.surrey.xw.api.JComponentWidget
import uk.ac.surrey.xw.api.JComponentWidgetKind
import uk.ac.surrey.xw.api.StateUpdater
import uk.ac.surrey.xw.api.StringProperty
import uk.ac.surrey.xw.api.WidgetKey

class NoteKind[W <: Note] extends JComponentWidgetKind[W] {
  override val name = "NOTE"
  override val newWidget = new Note(_, _, _)
  val textProperty = new StringProperty[W]("TEXT", _.setText(_), _.getText)
  val defaultProperty = Some(textProperty)
  override def propertySet = super.propertySet + textProperty
}

class Note(
  val key: WidgetKey,
  val stateUpdater: StateUpdater,
  ws: GUIWorkspace)
  extends JLabel
  with JComponentWidget {
  val kind = new NoteKind[this.type]
}
