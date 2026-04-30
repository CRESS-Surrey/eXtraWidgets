package uk.ac.surrey.xw.api

import java.awt.BorderLayout
import java.awt.Color.white

import javax.swing.JPanel
import javax.swing.JScrollPane

import org.nlogo.swing.TabLabel
import org.nlogo.window.GUIWorkspace

import uk.ac.surrey.xw.api.RichWorkspace.enrichWorkspace

class TabKind[W <: Tab] extends WidgetKind[W] {
  val newWidget = new Tab(_, _, _)
  val name = "TAB"
  val defaultProperty = None
  val colorProperty = new ColorProperty[W](
    "COLOR", Some(_.panel.setBackground(_)), _.panel.getBackground, white)
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
  val state: State,
  val ws: GUIWorkspace)
  extends JPanel
  with ExtraWidget {

  val kind: TabKind[this.type] = new TabKind[this.type]

  private var _order = 0d
  def setOrder(order: Double): Unit = {
    _order = order
    ws.reorderTabs(state)
  }
  def getOrder: Double = _order

  override def isOptimizedDrawingEnabled: Boolean = false

  val tabs = ws.tabs
  val tabManager = ws.tabManager

  setOpaque(true)

  val panel = new JPanel {
    setLayout(null)
    override def getPreferredSize: java.awt.Dimension =
      if (getComponents.nonEmpty) {
        val maxX = getComponents.map(c => c.getLocation.x + c.getSize.width).max
        val maxY = getComponents.map(c => c.getLocation.y + c.getSize.height).max
        new java.awt.Dimension(maxX, maxY)
      } else new java.awt.Dimension(0, 0)
  }
  setLayout(new BorderLayout)
  add(new JScrollPane(panel), BorderLayout.CENTER)

  addToAppTabs()

  private def index: Int =
    (0 until tabs.getTabCount)
      .find(i => tabs.getComponentAt(i) == this)
      .getOrElse(throw XWException("Tab " + key + " not in application tabs."))

  private var _title = ""
  def setTitle(title: String): Unit = {
    _title = title
    if ((0 until tabs.getTabCount).exists(i => tabs.getComponentAt(i) == this)) {
      tabs.setTitleAt(index, title)
      tabs.getTabLabelAt(index).foreach(_.setText(title))
      tabManager.updateTabActions()
    }
  }
  def getTitle: String = _title

  def addToAppTabs(): Unit =
    (0 until tabs.getTabCount)
      .find { i =>
        val component = tabs.getComponentAt(i)
        component != tabManager.interfaceTab && !component.isInstanceOf[Tab]
      }
      .foreach { i =>
        tabs.insertTab(_title, null, this, null, i)
        tabs.setTabComponentAt(i, new TabLabel(tabs, _title, this))
        tabManager.updateTabActions()
      }

  def removeFromAppTabs(): Unit = {
    if ((0 until tabs.getTabCount).exists(i => tabs.getComponentAt(i) == this)) {
      tabs.remove(this)
      tabManager.updateTabActions()
    }
  }

}
