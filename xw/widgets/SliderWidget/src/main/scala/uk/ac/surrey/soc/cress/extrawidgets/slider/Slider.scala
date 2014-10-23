package uk.ac.surrey.soc.cress.extrawidgets.slider

import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout.EAST
import java.awt.BorderLayout.NORTH

import org.nlogo.window.GUIWorkspace

import javax.swing.BorderFactory.createEmptyBorder
import javax.swing.JLabel
import javax.swing.JSlider
import uk.ac.surrey.soc.cress.extrawidgets.api.IntegerPropertyDef
import uk.ac.surrey.soc.cress.extrawidgets.api.LabeledPanelWidget
import uk.ac.surrey.soc.cress.extrawidgets.api.StateUpdater
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKey
import uk.ac.surrey.soc.cress.extrawidgets.api.swing.enrichSlider

class Slider(
  val key: WidgetKey,
  val stateUpdater: StateUpdater,
  ws: GUIWorkspace)
  extends LabeledPanelWidget {

  override def borderPadding = createEmptyBorder(0, 4, 0, 4)
  override def labelPosition = CENTER
  val slider = new JSlider()
  val valueLabel = new JLabel(slider.getValue.toString)
  add(slider, NORTH)
  add(valueLabel, EAST)

  val xwMinimum = new IntegerPropertyDef(this,
    slider.setMinimum(_),
    slider.getMinimum)

  val xwMaximum = new IntegerPropertyDef(this,
    slider.setMaximum(_),
    slider.getMaximum)

  val xwValue = new IntegerPropertyDef(this,
    slider.setValue(_),
    slider.getValue)

  slider.onStateChange { _ ⇒
    valueLabel.setText(xwValue.toString)
    xwValue.updateInState()
  }

}
