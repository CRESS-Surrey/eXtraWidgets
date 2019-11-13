package uk.ac.surrey.xw.checkbox

import javax.swing.JCheckBox

import org.nlogo.window.GUIWorkspace

import uk.ac.surrey.xw.api.AbstractButtonWidget
import uk.ac.surrey.xw.api.AbstractButtonWidgetKind
import uk.ac.surrey.xw.api.State
import uk.ac.surrey.xw.api.WidgetKey

class CheckboxKind[W <: Checkbox] extends AbstractButtonWidgetKind[W] {
  val name = "CHECKBOX"
  override val pluralName = "CHECKBOXES"
  override val defaultProperty = Some(selectedProperty)
  val newWidget = new Checkbox(_, _, _)
}

class Checkbox(
  val key: WidgetKey,
  val state: State,
  val ws: GUIWorkspace)
  extends JCheckBox
  with AbstractButtonWidget {
  override val kind = new CheckboxKind[this.type]
  setBorderPainted(true)
}
