package uk.ac.surrey.soc.cress.extrawidgets.core

import java.awt.Color.white

import org.nlogo.app.App
import org.nlogo.app.AppFrame
import org.nlogo.app.Tabs
import org.nlogo.swing.RichAction
import org.nlogo.window.GUIWorkspace

import javax.swing.JPanel
import uk.ac.surrey.soc.cress.extrawidgets.api.ExtraWidget
import uk.ac.surrey.soc.cress.extrawidgets.api.Kind
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyDef
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyKey
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyMap
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyValue
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKey
import uk.ac.surrey.soc.cress.extrawidgets.api.XWException

class TabKind extends Kind {

  val name = "TAB"

  def newInstance(key: WidgetKey, properties: PropertyMap, ws: GUIWorkspace) = {
    ws.getFrame.asInstanceOf[AppFrame].getLinkChildren.collectFirst {
      case app: App ⇒ new Tab(key, properties, app.tabs)
    }.getOrElse(throw new XWException("Tab widget can't access application tabs."))
  }

}

class Tab(
  val key: WidgetKey,
  properties: PropertyMap,
  tabs: Tabs)
  extends JPanel
  with ExtraWidget {

  object TitleProperty extends PropertyDef(this) {
    def set(newValue: PropertyValue, oldValue: Option[PropertyValue]): Unit =
      setTitle(newValue.toString)
    def unset = setTitle(w.key)
  }

  override def propertyDefs: Map[PropertyKey, PropertyDef[Tab]] =
    Map("TITLE" -> TitleProperty)

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
}
