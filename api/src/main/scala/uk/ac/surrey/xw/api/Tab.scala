package uk.ac.surrey.xw.api

import java.awt.Color.white

import org.nlogo.window.GUIWorkspace

import javax.swing.JPanel
import uk.ac.surrey.xw.api.RichWorkspace.enrichWorkspace

class TabKind[W <: Tab] extends WidgetKind[W] {
  val newWidget = new Tab(_, _, _)
  val name = "TAB"
  val defaultProperty = None
  val colorProperty = new ColorProperty[W](
    "COLOR", Some(_.setBackground(_)), _.getBackground, white)
  val titleProperty = new StringProperty[W](
    "TITLE", Some(_.setTitle(_)), _.getTitle)
  val enabledProperty = new BooleanProperty[W](
    "ENABLED", Some(_.setEnabled(_)), _.isEnabled, true)
  val orderProperty = new DoubleProperty[W](
    "ORDER", Some(_.setOrder(_)), _.getOrder, 0d
  )
  override def propertySet = super.propertySet ++ Set(
    titleProperty, colorProperty,
    enabledProperty, orderProperty
  )
}

class Tab(
  val key: WidgetKey,
  val stateUpdater: StateUpdater,
  val ws: GUIWorkspace)
  extends JPanel
  with ExtraWidget
  with ControlsChildrenEnabling {

  val kind = new TabKind[this.type]

  private var _order = 0d
  def setOrder(order: Double) = {
    _order = order
    ws.reorderTabs
  }
  def getOrder = _order

  override def isOptimizedDrawingEnabled = false

  val tabs = ws.tabs

  setLayout(null) // use absolute layout
  setOpaque(true)

  addToAppTabs()

  private def index: Int =
    (0 until tabs.getTabCount)
      .find(i ⇒ tabs.getComponentAt(i) == this)
      .getOrElse(throw XWException("Tab " + key + " not in application tabs."))

  private var _title = ""
  def setTitle(title: String): Unit = {
    _title = title
    tabs.setTitleAt(index, title)
    tabs.tabsMenu.getItem(index).setText(title)
  }
  def getTitle: String = _title

  def addToAppTabs(): Unit =
    (0 until tabs.getTabCount)
      .find { i ⇒
        tabs.getComponentAt(i) match {
          case _: org.nlogo.app.InterfaceTab ⇒ false
          case _: Tab ⇒ false
          case _ ⇒ true
        }
      }
      .foreach { i ⇒
        tabs.insertTab(_title, null, this, null, i)
        rebuildTabsMenu()
      }

  private def rebuildTabsMenu(): Unit = {
    tabs.tabsMenu.removeAll()
    for (i ← 0 until tabs.getTabCount)
      tabs.addMenuItem(i, tabs.getTitleAt(i))
  }

  def removeFromAppTabs(): Unit = {
    tabs.remove(this)
    tabs.removeMenuItem(index)
  }
}
