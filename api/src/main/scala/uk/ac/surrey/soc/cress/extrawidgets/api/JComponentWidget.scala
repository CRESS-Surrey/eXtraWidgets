package uk.ac.surrey.soc.cress.extrawidgets.api

import org.nlogo.swing.Utils.createWidgetBorder

import javax.swing.BorderFactory.createCompoundBorder
import javax.swing.BorderFactory.createEmptyBorder
import javax.swing.JComponent
import javax.swing.border.Border

trait JComponentWidget extends ComponentWidget {
  self: JComponent ⇒

  setHeight(25)
  setWidth(150)

  private val _borderPadding = createEmptyBorder(1, 1, 1, 1)
  def borderPadding: Border = _borderPadding

  private val _borderWhenOpaque =
    createCompoundBorder(createWidgetBorder, borderPadding)
  def borderWhenOpaque: Border = _borderWhenOpaque
  private val _borderWhenTransparent =
    createCompoundBorder(createEmptyBorder(3, 3, 2, 2), borderPadding)
  def borderWhenTransparent: Border = _borderWhenTransparent

  def updateBorder() =
    if (isOpaque) setBorder(borderWhenOpaque)
    else setBorder(borderWhenTransparent)

  val xwOpaque = new BooleanPropertyDef(this,
    (b) ⇒ { setOpaque(b); updateBorder() },
    isOpaque)

}
