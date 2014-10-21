package uk.ac.surrey.soc.cress.extrawidgets.slider

import java.awt.BorderLayout
import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout.LINE_END
import java.awt.BorderLayout.PAGE_START

import org.nlogo.window.GUIWorkspace
import org.nlogo.window.InterfaceColors.SLIDER_BACKGROUND

import javax.swing.BorderFactory.createEmptyBorder
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSlider
import javax.swing.SwingConstants
import uk.ac.surrey.soc.cress.extrawidgets.api.IntegerPropertyDef
import uk.ac.surrey.soc.cress.extrawidgets.api.JComponentWidget
import uk.ac.surrey.soc.cress.extrawidgets.api.StateUpdater
import uk.ac.surrey.soc.cress.extrawidgets.api.StringPropertyDef
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKey
import uk.ac.surrey.soc.cress.extrawidgets.api.const
import uk.ac.surrey.soc.cress.extrawidgets.api.swing.enrichSlider

class Slider(
  val key: WidgetKey,
  val stateUpdater: StateUpdater,
  ws: GUIWorkspace)
  extends JPanel
  with JComponentWidget {

  setLayout(new BorderLayout())

  override def borderPadding = createEmptyBorder(0, 4, 0, 4)

  private var text = key
  val slider = new JSlider()
  val textLabel = new JLabel(text) {
    setVerticalAlignment(SwingConstants.TOP)
  }
  val valueLabel = new JLabel(slider.getValue.toString) {
    setVerticalAlignment(SwingConstants.TOP)
  }
  add(slider, PAGE_START)
  add(textLabel, CENTER)
  add(valueLabel, LINE_END)

  override def defaultBackground = SLIDER_BACKGROUND

  val xwText = new StringPropertyDef(this,
    x ⇒ { text = x; textLabel.setText(text) },
    () ⇒ text,
    const(text))

  val xwMinimum = new IntegerPropertyDef(this,
    x ⇒ slider.setMinimum(x),
    slider.getMinimum,
    const(slider.getMinimum))

  val xwMaximum = new IntegerPropertyDef(this,
    x ⇒ slider.setMaximum(x),
    slider.getMaximum,
    const(slider.getMaximum))

  val xwValue = new IntegerPropertyDef(this,
    x ⇒ slider.setValue(x),
    slider.getValue,
    const(slider.getValue))

  slider.onStateChange { _ ⇒
    val value = slider.getValue
    valueLabel.setText(value.toString)
    xwValue.setValue(Int.box(value))
  }

}
