package uk.ac.surrey.xw.slider

import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout.EAST
import java.awt.BorderLayout.NORTH

import javax.swing.BorderFactory.createEmptyBorder
import javax.swing.JLabel
import javax.swing.JSlider

import scala.language.reflectiveCalls

import org.nlogo.api.Dump
import org.nlogo.api.MultiErrorHandler
import org.nlogo.window.GUIWorkspace
import org.nlogo.window.SliderData

import uk.ac.surrey.xw.api.DoubleProperty
import uk.ac.surrey.xw.api.LabeledPanelWidget
import uk.ac.surrey.xw.api.LabeledPanelWidgetKind
import uk.ac.surrey.xw.api.State
import uk.ac.surrey.xw.api.StringProperty
import uk.ac.surrey.xw.api.WidgetKey
import uk.ac.surrey.xw.api.swing.enrichSlider

class SliderKind[W <: Slider] extends LabeledPanelWidgetKind[W] {
  override val name = "SLIDER"
  override val newWidget = new Slider(_, _, _)
  val valueProperty = new DoubleProperty[W](
    "VALUE", Some(_.sliderData.setValue(_)), _.sliderData.value, 50)
  override val defaultProperty = Some(valueProperty)
  override val propertySet = super.propertySet ++ Set(valueProperty,
    new StringProperty[W]("UNITS",
      Some(_.setUnits(_)), _.units),
    new DoubleProperty[W]("MINIMUM",
      Some(_.sliderData.setMinimum(_)), _.sliderData.minimum, 0d),
    new DoubleProperty[W]("MAXIMUM",
      Some(_.sliderData.setMaximum(_)), _.sliderData.maximum, 100d),
    new DoubleProperty[W]("INCREMENT",
      Some(_.sliderData.setIncrement(_)), _.sliderData.increment, 1d)
  )
}

class Slider(
  val key: WidgetKey,
  val state: State,
  val ws: GUIWorkspace)
  extends LabeledPanelWidget {

  override val kind = new SliderKind[this.type]

  class Data extends SliderData(new MultiErrorHandler {}) {
    def toTicks(v: Double): Int = math.round((v - minimum) / increment).toInt
    def fromTicks(ticks: Int): Double = minimum + (ticks * increment)
    def update(v: Double): Boolean = valueSetter(coerceValue(v))
    def updateFromTicks(ticks: Int): Boolean = update(fromTicks(ticks))
    def maxToTicks: Int = toTicks(maximum)
    def valueToTicks: Int = toTicks(value)
    def setIncrement(inc: Double): Unit = { increment = inc; slider.updateFromData() }
    def setMinimum(min: Double): Unit = { minimum = min; slider.updateFromData() }
    def setMaximum(max: Double): Unit = { maximum = max; slider.updateFromData() }
    def setValue(v: Double): Unit = { value = v; slider.updateFromData() }
  }

  val sliderData = new Data()

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

  private var _units = ""
  def setUnits(units: String): Unit = { _units = units; valueLabel.update() }
  def units = _units

  slider.onStateChange { _ ⇒
    sliderData.updateFromTicks(slider.getValue)
    valueLabel.update()
    updateInState(kind.valueProperty)
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
