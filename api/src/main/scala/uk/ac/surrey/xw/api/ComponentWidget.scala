package uk.ac.surrey.xw.api

import java.awt.Component

import org.nlogo.awt.Fonts.adjustDefaultFont

trait ComponentWidget extends ExtraWidget {

  self: Component ⇒

  adjustDefaultFont(this)

  val xwX = new IntegerProperty(setX(_), getX)
  val xwY = new IntegerProperty(setY(_), getY)
  val xwWidth = new IntegerProperty(setWidth(_), getWidth)
  val xwHeight = new IntegerProperty(setHeight(_), getHeight)
  val xwHidden = new BooleanProperty(b ⇒ setVisible(!b), () ⇒ !isVisible)
  val xwBackground = new ColorProperty(setBackground, getBackground)
  val xwForeground = new ColorProperty(setForeground, getForeground)

  def setX(x: Int): Unit = setBounds(x, getY, getWidth, getHeight)
  def setY(y: Int): Unit = setBounds(getX, y, getWidth, getHeight)
  def setWidth(width: Int): Unit = setBounds(getX, getY, width, getHeight)
  def setHeight(height: Int): Unit = setBounds(getX, getY, getWidth, height)
}
