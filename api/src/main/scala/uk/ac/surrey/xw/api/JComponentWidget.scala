package uk.ac.surrey.xw.api

import javax.swing.BorderFactory.createCompoundBorder
import javax.swing.BorderFactory.createEmptyBorder
import javax.swing.BorderFactory.createLineBorder
import javax.swing.JComponent
import javax.swing.border.Border

import org.nlogo.theme.InterfaceColors

abstract class JComponentWidgetKind[W <: JComponentWidget]
  extends ComponentWidgetKind[W] {
  val opaqueProperty = new BooleanProperty[W](
    "OPAQUE", Some((w, b) => { w.setOpaque(b); w.updateBorder() }), _.isOpaque, true)
  override def propertySet = super.propertySet ++ Set(opaqueProperty)
}

trait JComponentWidget extends JComponent with ComponentWidget {

  private val _borderPadding = createEmptyBorder(2, 4, 3, 4)
  def borderPadding: Border = _borderPadding

  // Keep this as a plain Swing border plus padding. Swing's rounded LineBorder
  // is barely rounded at 1px and inconsistent with opaque component
  // backgrounds; real rounded corners would require custom painting or a
  // NetLogo widget panel mixin, which is more machinery than xw needs here.
  def borderWhenOpaque: Border =
    createCompoundBorder(createLineBorder(ThemeColors.widgetBorder, 1), borderPadding)
  def borderWhenTransparent: Border =
    createCompoundBorder(createLineBorder(InterfaceColors.Transparent, 1), borderPadding)

  def updateBorder(): Unit =
    if (isOpaque) setBorder(borderWhenOpaque)
    else setBorder(borderWhenTransparent)
}
