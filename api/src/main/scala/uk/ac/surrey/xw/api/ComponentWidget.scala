package uk.ac.surrey.xw.api

import java.awt.Color
import java.awt.Color.black

import org.nlogo.awt.Fonts.adjustDefaultFont
import org.nlogo.window.InterfaceColors.SLIDER_BACKGROUND

import uk.ac.surrey.xw.api.RichWorkspace.enrichWorkspace
import uk.ac.surrey.xw.api.swing.enrichComponent

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
    "TEXT-COLOR", Some(_.setTextColor(_)), _.getTextColor, black)
  val fontSizeProperty = new IntegerProperty[W](
    "FONT-SIZE", Some(_.fontSize = _), _.fontSize, 12)
  override def propertySet = super.propertySet ++ Set(
    tabProperty, xProperty, yProperty,
    widthProperty, heightProperty,
    hiddenProperty, enabledProperty,
    colorProperty, textColorProperty,
    fontSizeProperty)
}

trait ComponentWidget extends ExtraWidget {

  adjustDefaultFont(this)

  def setX(x: Int): Unit = setLocation(x, getY)
  def setY(y: Int): Unit = setLocation(getX, y)
  def setWidth(width: Int): Unit = setSize(width, getHeight)
  def setHeight(height: Int): Unit = setSize(getWidth, height)

  def tab: Option[Tab] = ws.xwTabs.find(_.panel.getComponents.contains(this))
  def getTabKey: PropertyKey = tab
    .getOrElse(throw XWException("Widget " + key + " is not on any tab."))
    .key
  def setTab(tabKey: WidgetKey): Unit =
    ws.xwTabs.find(_.key == tabKey) match {
      case None ⇒ throw XWException("Tab " + tabKey + " does not exist.")
      case Some(newTab) ⇒
        tab match {
          case None ⇒
            newTab.panel.add(this)
          case Some(oldTab) if oldTab.key != tabKey ⇒
            oldTab.panel.remove(this)
            newTab.panel.add(this)
        }
    }

  private var _textColor: Color = getForeground
  def getTextColor = _textColor
  def setTextColor(textColor: Color): Unit = {
    _textColor = textColor
    setForeground(_textColor)
    this.allChildren.foreach(_.setForeground(_textColor))
  }

  private var _fontSize: Int = 0
  def fontSize = _fontSize
  def fontSize_=(size: Int) {
    if (size < 1) throw new IllegalStateException(
      "Cannot use a font size smaller than 1" +
      " for widget " + key + "."
    )
    _fontSize = size
    val newFont = getFont.deriveFont(size.toFloat)
    setFont(newFont)
    this.allChildren.foreach(_.setFont(newFont))
  }
}
