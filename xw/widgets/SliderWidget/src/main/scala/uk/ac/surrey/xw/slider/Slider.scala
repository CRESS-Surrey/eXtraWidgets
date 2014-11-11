package uk.ac.surrey.xw.slider

import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout.EAST
import java.awt.BorderLayout.NORTH

import org.nlogo.api.Dump
import org.nlogo.window.GUIWorkspace
import org.nlogo.window.SliderData

import javax.swing.BorderFactory.createEmptyBorder
import javax.swing.JLabel
import javax.swing.JSlider
import uk.ac.surrey.xw.api.DoubleProperty
import uk.ac.surrey.xw.api.LabeledPanelWidget
import uk.ac.surrey.xw.api.StateUpdater
import uk.ac.surrey.xw.api.StringProperty
import uk.ac.surrey.xw.api.WidgetKey
import uk.ac.surrey.xw.api.annotations.DefaultProperty
import uk.ac.surrey.xw.api.swing.enrichSlider

@DefaultProperty("VALUE")
class Slider(
  val key: WidgetKey,
  val stateUpdater: StateUpdater,
  ws: GUIWorkspace)
  extends LabeledPanelWidget {

  val sliderData = new SliderData() {
    def toTicks(v: Double): Int = math.round((v - minimum) / increment).toInt
    def fromTicks(ticks: Int): Double = minimum + (ticks * increment)
    def update(v: Double): Boolean = valueSetter(coerceValue(v))
    def updateFromTicks(ticks: Int): Boolean = update(fromTicks(ticks))
    def maxToTicks: Int = toTicks(maximum)
    def valueToTicks: Int = toTicks(value)
  }

  val slider = new JSlider() {
    minorTickSpacing = 1
    snapToTicks = true
    updateFromData()
    def updateFromData(): Unit = {
      val newMax = sliderData.maxToTicks
      val newValue = sliderData.valueToTicks
      setMaximum(newMax)
      setValue(newValue)
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
  val xwUnits = new StringProperty(
    u ⇒ { units = u; valueLabel.update() },
    () ⇒ units)

  val xwMinimum = new DoubleProperty(
    min ⇒ { sliderData.minimum = min; slider.updateFromData() },
    () ⇒ sliderData.minimum)

  val xwMaximum = new DoubleProperty(
    max ⇒ { sliderData.maximum = max; slider.updateFromData() },
    () ⇒ sliderData.maximum)

  val xwValue = new DoubleProperty(
    v ⇒ if (sliderData.update(v)) slider.updateFromData(),
    () ⇒ sliderData.value)

  val xwIncrement = new DoubleProperty(
    inc ⇒ { sliderData.increment = inc; slider.updateFromData() },
    () ⇒ sliderData.increment)

  slider.onStateChange { _ ⇒
    sliderData.updateFromTicks(slider.getValue)
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
