package uk.ac.surrey.soc.cress.extrawidgets.api

import java.awt.Component

import org.nlogo.awt.Fonts.adjustDefaultFont

trait ComponentWidget extends ExtraWidget {

  self: Component ⇒

  adjustDefaultFont(this)

  val xwX = new IntegerPropertyDef(this, x ⇒ setX(x), getX)
  val xwY = new IntegerPropertyDef(this, y ⇒ setY(y), getY)
  val xwWidth = new IntegerPropertyDef(this, w ⇒ setWidth(w), getWidth)
  val xwHeight = new IntegerPropertyDef(this, h ⇒ setHeight(h), getHeight)
  val xwHidden = new BooleanPropertyDef(this, b ⇒ setVisible(!b), () ⇒ !isVisible)
  val xwBackground = new ColorPropertyDef(this, setBackground, getBackground)
  val xwForeground = new ColorPropertyDef(this, setForeground, getForeground)

  def setX(x: Int): Unit = setBounds(x, getY, getWidth, getHeight)
  def setY(y: Int): Unit = setBounds(getX, y, getWidth, getHeight)
  def setWidth(width: Int): Unit = setBounds(getX, getY, width, getHeight)
  def setHeight(height: Int): Unit = setBounds(getX, getY, getWidth, height)
}
