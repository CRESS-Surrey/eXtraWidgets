package uk.ac.surrey.soc.cress.extrawidgets.gui

import java.awt.Color.white

import org.nlogo.app.App
import org.nlogo.app.AppFrame
import org.nlogo.window.GUIWorkspace

import javax.swing.JPanel
import uk.ac.surrey.soc.cress.extrawidgets
import uk.ac.surrey.soc.cress.extrawidgets.api.ColorPropertyDef
import uk.ac.surrey.soc.cress.extrawidgets.api.JComponentWidget
import uk.ac.surrey.soc.cress.extrawidgets.api.StateUpdater
import uk.ac.surrey.soc.cress.extrawidgets.api.StringPropertyDef
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKey
import uk.ac.surrey.soc.cress.extrawidgets.api.XWException

class Tab(
  val key: WidgetKey,
  val stateUpdater: StateUpdater,
  ws: GUIWorkspace)
  extends JPanel
  with JComponentWidget {

  override def isOptimizedDrawingEnabled = false

  val xwTitle = new StringPropertyDef(this, setTitle, getTitle _)
  override val xwBackground = new ColorPropertyDef(this, setBackground, getBackground)

  val tabs = ws.getFrame.asInstanceOf[AppFrame].getLinkChildren
    .collectFirst { case app: App ⇒ app.tabs }
    .getOrElse(throw new XWException("Tab widget can't access application tabs."))

  setBackground(white)
  setLayout(null) // use absolute layout

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
          case _: extrawidgets.gui.Tab ⇒ false
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
