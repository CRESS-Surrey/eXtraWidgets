package uk.ac.surrey.xw.api

import java.awt.Color.black
import java.awt.Component

import org.nlogo.awt.Fonts.adjustDefaultFont
import org.nlogo.window.InterfaceColors.SLIDER_BACKGROUND

abstract class ComponentWidgetKind[W <: ComponentWidget] extends WidgetKind[W] {
  val xProperty = new IntegerProperty[W](
    "X", _.setX(_), _.getX)
  val yProperty = new IntegerProperty[W](
    "Y", _.setY(_), _.getY)
  val widthProperty = new IntegerProperty[W](
    "WIDTH", _.setWidth(_), _.getWidth, 150)
  val heightProperty = new IntegerProperty[W](
    "HEIGHT", _.setHeight(_), _.getHeight, 25)
  val hiddenProperty = new BooleanProperty[W](
    "HIDDEN", (w, b) ⇒ w.setVisible(!b), !_.isVisible)
  val colorProperty = new ColorProperty[W](
    "COLOR", _.setBackground(_), _.getBackground, SLIDER_BACKGROUND)
  val textColorProperty = new ColorProperty[W](
    "TEXT-COLOR", _.setForeground(_), _.getForeground, black)
  def propertySet = Set(
    xProperty, yProperty, widthProperty, heightProperty,
    hiddenProperty, colorProperty, textColorProperty)
}

trait ComponentWidget extends ExtraWidget {
  self: Component ⇒
  adjustDefaultFont(this)
  def setX(x: Int): Unit = setBounds(x, getY, getWidth, getHeight)
  def setY(y: Int): Unit = setBounds(getX, y, getWidth, getHeight)
  def setWidth(width: Int): Unit = setBounds(getX, getY, width, getHeight)
  def setHeight(height: Int): Unit = setBounds(getX, getY, getWidth, height)
}
