package uk.ac.surrey.soc.cress.extrawidgets.gui

import java.awt.Color.white
import org.nlogo.app.App
import org.nlogo.app.AppFrame
import org.nlogo.swing.RichAction
import org.nlogo.window.GUIWorkspace
import javax.swing.JPanel
import uk.ac.surrey.soc.cress.extrawidgets.api.ColorPropertyDef
import uk.ac.surrey.soc.cress.extrawidgets.api.JComponentWidget
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyMap
import uk.ac.surrey.soc.cress.extrawidgets.api.StringPropertyDef
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKey
import uk.ac.surrey.soc.cress.extrawidgets.api.XWException
import uk.ac.surrey.soc.cress.extrawidgets.api.StateUpdater

class Tab(
  val key: WidgetKey,
  val stateUpdater: StateUpdater,
  ws: GUIWorkspace)
  extends JPanel
  with JComponentWidget {

  override def isOptimizedDrawingEnabled = false
  override def defaultOpacity = true

  val xwTitle = new StringPropertyDef(this, v ⇒ setTitle(v), () ⇒ getTitle, () ⇒ key)
  override val xwBackground = new ColorPropertyDef(this, setBackground, getBackground, () ⇒ white)

  val tabs = ws.getFrame.asInstanceOf[AppFrame].getLinkChildren
    .collectFirst { case app: App ⇒ app.tabs }
    .getOrElse(throw new XWException("Tab widget can't access application tabs."))

  setLayout(null) // use absolute layout

  addToAppTabs(xwTitle.getter())

  def addToAppTabs(title: String): Unit = {
    tabs.addTab(title.toString, this)
    val i = tabs.tabsMenu.getItemCount
    tabs.tabsMenu.addMenuItem(
      key, ('1' + i).toChar,
      RichAction { _ ⇒ tabs.setSelectedIndex(i) })
  }

  private def tabIndex: Option[Int] =
    (0 until tabs.getTabCount).find(i ⇒ tabs.getComponentAt(i) == this)

  def setTitle(title: String) =
    for (i ← tabIndex) {
      tabs.setTitleAt(i, title)
      tabs.tabsMenu.getItem(i).setText(title)
    }

  def getTitle: String = tabIndex.map(tabs.getTitleAt(_)).getOrElse(xwTitle.default())

}
