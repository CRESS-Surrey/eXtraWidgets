package uk.ac.surrey.soc.cress.extrawidgets.slider

import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout.EAST
import java.awt.BorderLayout.NORTH

import org.nlogo.api.Dump
import org.nlogo.window.GUIWorkspace
import org.nlogo.window.SliderData

import javax.swing.BorderFactory.createEmptyBorder
import javax.swing.JLabel
import javax.swing.JSlider
import uk.ac.surrey.soc.cress.extrawidgets.api.DoublePropertyDef
import uk.ac.surrey.soc.cress.extrawidgets.api.LabeledPanelWidget
import uk.ac.surrey.soc.cress.extrawidgets.api.StateUpdater
import uk.ac.surrey.soc.cress.extrawidgets.api.StringPropertyDef
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKey
import uk.ac.surrey.soc.cress.extrawidgets.api.swing.enrichSlider

class Slider(
  val key: WidgetKey,
  val stateUpdater: StateUpdater,
  ws: GUIWorkspace)
  extends LabeledPanelWidget {

  val sliderData = new SliderData() {
    def update(v: Double) = valueSetter(coerceValue(v))
  }

  val slider = new JSlider() {
    minorTickSpacing = 1
    snapToTicks = true
    updateFromData()
    def updateFromData(): Unit = {
      val nbUnits = (sliderData.maximum - sliderData.minimum) / sliderData.increment
      setMaximum(nbUnits.intValue)
      setValue((sliderData.value / sliderData.increment).intValue)
    }
  }
  add(slider, NORTH)

  override def borderPadding = createEmptyBorder(0, 4, 0, 4)
  override def labelPosition = CENTER

  val valueLabel = new JLabel() {
    def update(): Unit = setText(valueString(sliderData.value))
    update()
  }
  add(valueLabel, EAST)

  private var units = ""
  val xwUnits = new StringPropertyDef(
    u ⇒ { units = u; valueLabel.update() },
    () ⇒ units)

  val xwMinimum = new DoublePropertyDef(
    min ⇒ { sliderData.minimum = min; slider.updateFromData() },
    () ⇒ sliderData.minimum)

  val xwMaximum = new DoublePropertyDef(
    max ⇒ { sliderData.maximum = max; slider.updateFromData() },
    () ⇒ sliderData.maximum)

  val xwValue = new DoublePropertyDef(
    v ⇒ if (sliderData.update(v)) slider.updateFromData(),
    () ⇒ sliderData.value)

  val xwIncrement = new DoublePropertyDef(
    inc ⇒ { sliderData.increment = inc; slider.updateFromData() },
    () ⇒ sliderData.increment)

  slider.onStateChange { _ ⇒
    sliderData.update(slider.getValue * sliderData.increment)
    valueLabel.update()
    updateInState(xwValue)
  }

  /** copied from org.nlogo.window.AbstractSliderWidget */
  def valueString(num: Double): String = {
    var numString = Dump.number(num)
    var place = numString.indexOf('.')
    val p = sliderData.precision
    if (p > 0 && place == -1) {
      numString += "."
      place = numString.length - 1
    }
    if (place != -1 && numString.indexOf('E') == -1) {
      val padding = p - (numString.length - place - 1)
      numString = numString + ("0" * padding)
    }
    if (units == "") numString else numString + " " + units
  }
}
