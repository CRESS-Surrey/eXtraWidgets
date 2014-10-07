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

class NoteKind extends Kind {
  type W = Note
  val name = "NOTE"
  override def newInstance(key: WidgetKey, properties: PropertyMap, ws: GUIWorkspace) = {
    new Note(key, properties)
  }
}

class Note(
  val key: WidgetKey,
  properties: PropertyMap)
  extends JLabel
  with ExtraWidget {
  println("Contructing Note: " + this.toString)

  object Text extends PropertyDef(this) {
    def set(newValue: PropertyValue, oldValue: Option[PropertyValue]): Unit =
      setText(newValue.toString)
    def unset() = setText("")
  }

  override val propertyDefs = Map(
    "TEXT" -> Text
  )
}
