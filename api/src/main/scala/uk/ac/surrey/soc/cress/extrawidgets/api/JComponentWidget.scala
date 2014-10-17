package uk.ac.surrey.soc.cress.extrawidgets.api

import org.nlogo.swing.Utils.createWidgetBorder

import javax.swing.BorderFactory.createCompoundBorder
import javax.swing.BorderFactory.createEmptyBorder
import javax.swing.JComponent

trait JComponentWidget extends ComponentWidget {
  self: JComponent ⇒

  private val _borderPadding = createEmptyBorder(1, 1, 1, 1)
  def borderPadding = _borderPadding

  private val _borderWhenOpaque =
    createCompoundBorder(createWidgetBorder, borderPadding)
  def borderWhenOpaque = _borderWhenOpaque
  private val _borderWhenTransparent =
    createCompoundBorder(createEmptyBorder(3, 3, 2, 2), borderPadding)
  def borderWhenTransparent = _borderWhenTransparent

  def updateBorder() =
    if (isOpaque) setBorder(borderWhenOpaque)
    else setBorder(borderWhenTransparent)

  private val _defaultOpacity = isOpaque
  def defaultOpacity = _defaultOpacity
  val xwOpaque = new BooleanPropertyDef(this,
    (b) ⇒ { setOpaque(b); updateBorder() },
    isOpaque,
    () ⇒ defaultOpacity)

}
