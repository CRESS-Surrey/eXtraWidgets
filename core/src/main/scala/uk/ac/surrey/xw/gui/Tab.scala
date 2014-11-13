package uk.ac.surrey.xw.gui

import java.awt.Color.white

import org.nlogo.app.App
import org.nlogo.app.AppFrame
import org.nlogo.awt.Fonts.adjustDefaultFont
import org.nlogo.window.GUIWorkspace

import javax.swing.JPanel
import uk.ac.surrey.xw.api.ColorProperty
import uk.ac.surrey.xw.api.ExtraWidget
import uk.ac.surrey.xw.api.StateUpdater
import uk.ac.surrey.xw.api.StringProperty
import uk.ac.surrey.xw.api.WidgetKey
import uk.ac.surrey.xw.api.WidgetKind
import uk.ac.surrey.xw.api.XWException

class TabKind[W <: Tab] extends WidgetKind[W] {
  val newWidget = new Tab(_, _, _)
  val name = "TAB"
  val defaultProperty = None
  val colorProperty = new ColorProperty[W](
    "COLOR", _.setBackground(_), _.getBackground, white)
  val titleProperty = new StringProperty[W]("TITLE", _.setTitle(_), _.getTitle)
  override def propertySet = Set(titleProperty, colorProperty)
}

class Tab(
  val key: WidgetKey,
  val stateUpdater: StateUpdater,
  ws: GUIWorkspace)
  extends JPanel
  with ExtraWidget {

  adjustDefaultFont(this)

  val kind = new TabKind[this.type]

  override def isOptimizedDrawingEnabled = false

  val tabs = ws.getFrame.asInstanceOf[AppFrame].getLinkChildren
    .collectFirst { case app: App ⇒ app.tabs }
    .getOrElse(throw new XWException("Tab widget can't access application tabs."))

  setLayout(null) // use absolute layout
  setOpaque(true)

  addToAppTabs()

  private def index: Int =
    (0 until tabs.getTabCount)
      .find(i ⇒ tabs.getComponentAt(i) == this)
      .getOrElse(throw XWException("Tab " + key + " not in application tabs."))

  def setTitle(title: String): Unit = {
    tabs.setTitleAt(index, title)
    tabs.tabsMenu.getItem(index).setText(title)
  }

  def getTitle: String = tabs.getTitleAt(index)

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
        tabs.insertTab(key, null, this, null, i)
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
