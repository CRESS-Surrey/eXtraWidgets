package uk.ac.surrey.xw.checkbox

import org.nlogo.window.GUIWorkspace
import org.nlogo.window.InterfaceColors.SWITCH_BACKGROUND
import javax.swing.JCheckBox
import uk.ac.surrey.xw.api.AbstractButtonWidget
import uk.ac.surrey.xw.api.StateUpdater
import uk.ac.surrey.xw.api.WidgetKey
import uk.ac.surrey.xw.api.annotations.DefaultProperty
import uk.ac.surrey.xw.api.annotations.PluralName

@DefaultProperty("SELECTED?")
@PluralName("CHECKBOXES")
class Checkbox(
  val key: WidgetKey,
  val stateUpdater: StateUpdater,
  ws: GUIWorkspace)
  extends JCheckBox
  with AbstractButtonWidget {

  setBorderPainted(true)
  setBackground(SWITCH_BACKGROUND)
  setOpaque(true)

}
