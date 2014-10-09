package uk.ac.surrey.soc.cress.extrawidgets.gui

import java.awt.Color.white

import org.nlogo.app.App
import org.nlogo.app.AppFrame
import org.nlogo.swing.RichAction
import org.nlogo.window.GUIWorkspace

import javax.swing.JPanel
import uk.ac.surrey.soc.cress.extrawidgets.api.ExtraWidget
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyMap
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKey
import uk.ac.surrey.soc.cress.extrawidgets.api.XWException
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyDef

class TitleProperty(tab: Tab) extends PropertyDef(tab) {
  type ValueType = String
  def setValue(newValue: String, oldValue: Option[String]): Unit =
    tab.setTitle(newValue.toString)
  def defaultValue = tab.key
  override def getValue = tab.getTitle
}

class Tab(
  val key: WidgetKey,
  properties: PropertyMap,
  ws: GUIWorkspace)
  extends JPanel
  with ExtraWidget {

  val title = new TitleProperty(this)

  println("Contructing Tab " + key + ": " + this.toString)

  val tabs = ws.getFrame.asInstanceOf[AppFrame].getLinkChildren
    .collectFirst { case app: App ⇒ app.tabs }
    .getOrElse(throw new XWException("Tab widget can't access application tabs."))

  setLayout(null) // use absolute layout
  setBackground(white)

  for (title ← properties.get("TITLE").orElse(Some(key))) {
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

  def getTitle: String = tabIndex.map(tabs.getTitleAt(_)).getOrElse(title.defaultValue)

}
