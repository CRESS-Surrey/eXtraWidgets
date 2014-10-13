package uk.ac.surrey.soc.cress.extrawidgets.api

import java.awt.Component

trait ComponentWidget extends ExtraWidget {

  self: Component ⇒

  val xwX = new IntegerPropertyDef(this, x ⇒ setX(x), getX, const(getX))
  val xwY = new IntegerPropertyDef(this, y ⇒ setY(y), getY, const(getY))
  val xwWidth = new IntegerPropertyDef(this, w ⇒ setWidth(w), getWidth, const(getWidth))
  val xwHeight = new IntegerPropertyDef(this, h ⇒ setHeight(h), getHeight, const(getHeight))
  val xwHidden = new BooleanPropertyDef(this, b ⇒ setVisible(!b), () ⇒ !isVisible, const(!isVisible))
  val xwBackground = new ColorPropertyDef(this, setBackground, getBackground, const(getBackground))
  val xwForeground = new ColorPropertyDef(this, setForeground, getForeground, const(getForeground))

  def setX(x: Int): Unit = setBounds(x, getY, getWidth, getHeight)
  def setY(y: Int): Unit = setBounds(getX, y, getWidth, getHeight)
  def setWidth(width: Int): Unit = setBounds(getX, getY, width, getHeight)
  def setHeight(height: Int): Unit = setBounds(getX, getY, getWidth, height)
}
