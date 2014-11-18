package uk.ac.surrey.xw.api

import java.awt.Color.black

import org.nlogo.awt.Fonts.adjustDefaultFont
import org.nlogo.window.InterfaceColors.SLIDER_BACKGROUND

import uk.ac.surrey.xw.api.RichWorkspace.enrichWorkspace

abstract class ComponentWidgetKind[W <: ComponentWidget] extends WidgetKind[W] {
  val tabProperty = new StringProperty[W](
    "TAB", Some(_.setTab(_)), _.getTabKey)
  val enabledProperty = new BooleanProperty[W](
    "ENABLED", Some(_.setEnabled(_)), _.isEnabled, true)
  val xProperty = new IntegerProperty[W](
    "X", Some(_.setX(_)), _.getX)
  val yProperty = new IntegerProperty[W](
    "Y", Some(_.setY(_)), _.getY)
  val widthProperty = new IntegerProperty[W](
    "WIDTH", Some(_.setWidth(_)), _.getWidth, 150)
  val heightProperty = new IntegerProperty[W](
    "HEIGHT", Some(_.setHeight(_)), _.getHeight, 25)
  val hiddenProperty = new BooleanProperty[W](
    "HIDDEN", Some((w, b) ⇒ w.setVisible(!b)), !_.isVisible)
  val colorProperty = new ColorProperty[W](
    "COLOR", Some(_.setBackground(_)), _.getBackground, SLIDER_BACKGROUND)
  val textColorProperty = new ColorProperty[W](
    "TEXT-COLOR", Some(_.setForeground(_)), _.getForeground, black)
  override def propertySet = super.propertySet ++ Set(
    tabProperty, xProperty, yProperty,
    widthProperty, heightProperty,
    hiddenProperty, enabledProperty,
    colorProperty, textColorProperty)
}

trait ComponentWidget
  extends ExtraWidget
  with ControlsChildrenEnabling {
  adjustDefaultFont(this)
  def setX(x: Int): Unit = setBounds(x, getY, getWidth, getHeight)
  def setY(y: Int): Unit = setBounds(getX, y, getWidth, getHeight)
  def setWidth(width: Int): Unit = setBounds(getX, getY, width, getHeight)
  def setHeight(height: Int): Unit = setBounds(getX, getY, getWidth, height)
  def tab: Option[Tab] = ws.xwTabs.find(_.getComponents.contains(this))
  def getTabKey: PropertyKey = tab
    .getOrElse(throw XWException("Widget " + key + " is not on any tab."))
    .key
  def setTab(tabKey: WidgetKey): Unit =
    ws.xwTabs.find(_.key == tabKey) match {
      case None ⇒ throw XWException("Tab " + tabKey + " does not exist.")
      case Some(newTab) ⇒
        tab match {
          case None ⇒
            newTab.add(this)
          case Some(oldTab) if oldTab.key != tabKey ⇒
            oldTab.remove(this)
            newTab.add(this)
        }
    }
}
