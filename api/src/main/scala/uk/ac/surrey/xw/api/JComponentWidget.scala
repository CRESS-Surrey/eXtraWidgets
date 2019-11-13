package uk.ac.surrey.xw.api

import javax.swing.BorderFactory.createCompoundBorder
import javax.swing.BorderFactory.createEmptyBorder
import javax.swing.JComponent
import javax.swing.border.Border

import org.nlogo.swing.Utils.createWidgetBorder

abstract class JComponentWidgetKind[W <: JComponentWidget]
  extends ComponentWidgetKind[W] {
  val opaqueProperty = new BooleanProperty[W](
    "OPAQUE", Some((w, b) ⇒ { w.setOpaque(b); w.updateBorder() }), _.isOpaque, true)
  override def propertySet = super.propertySet ++ Set(opaqueProperty)
}

trait JComponentWidget extends JComponent with ComponentWidget {

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
}
