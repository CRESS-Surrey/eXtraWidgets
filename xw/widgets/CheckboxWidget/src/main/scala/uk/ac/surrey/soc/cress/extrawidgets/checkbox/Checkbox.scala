package uk.ac.surrey.soc.cress.extrawidgets.checkbox

import org.nlogo.window.GUIWorkspace
import org.nlogo.window.InterfaceColors.SWITCH_BACKGROUND

import javax.swing.JCheckBox
import uk.ac.surrey.soc.cress.extrawidgets.api.BooleanPropertyDef
import uk.ac.surrey.soc.cress.extrawidgets.api.JComponentWidget
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyMap
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKey
import uk.ac.surrey.soc.cress.extrawidgets.api.const
import uk.ac.surrey.soc.cress.extrawidgets.api.AbstractButtonWidget

class Checkbox(val key: WidgetKey, properties: PropertyMap, ws: GUIWorkspace)
  extends JCheckBox with AbstractButtonWidget {

  setBorderPainted(true)
  override def defaultBackground = SWITCH_BACKGROUND

}
