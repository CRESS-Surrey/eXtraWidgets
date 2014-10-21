package uk.ac.surrey.soc.cress.extrawidgets.checkbox

import org.nlogo.window.GUIWorkspace
import org.nlogo.window.InterfaceColors.SWITCH_BACKGROUND

import javax.swing.JCheckBox
import uk.ac.surrey.soc.cress.extrawidgets.api.AbstractButtonWidget
import uk.ac.surrey.soc.cress.extrawidgets.api.StateUpdater
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKey

class Checkbox(
  val key: WidgetKey,
  val stateUpdater: StateUpdater,
  ws: GUIWorkspace)
  extends JCheckBox
  with AbstractButtonWidget {

  setBorderPainted(true)
  override def defaultBackground = SWITCH_BACKGROUND

}
