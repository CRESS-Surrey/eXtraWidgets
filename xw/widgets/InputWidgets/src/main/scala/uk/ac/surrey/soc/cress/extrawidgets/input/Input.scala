package uk.ac.surrey.soc.cress.extrawidgets.input

import java.awt.BorderLayout.CENTER

import org.nlogo.window.GUIWorkspace

import javax.swing.JTextField
import uk.ac.surrey.soc.cress.extrawidgets.api.LabeledPanelWidget
import uk.ac.surrey.soc.cress.extrawidgets.api.StateUpdater
import uk.ac.surrey.soc.cress.extrawidgets.api.StringPropertyDef
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKey

class TextInput(
  val key: WidgetKey,
  val stateUpdater: StateUpdater,
  ws: GUIWorkspace)
  extends LabeledPanelWidget {

  val textField = new JTextField()
  add(textField, CENTER)

  val xwText = new StringPropertyDef(this,
    textField.setText,
    textField.getText
  )
}
