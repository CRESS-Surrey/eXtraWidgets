package uk.ac.surrey.soc.cress.extrawidgets.core

import org.nlogo.api.Syntax.StringType
import org.nlogo.app.App
import org.nlogo.app.AppFrame
import org.nlogo.app.Tabs
import org.nlogo.swing.RichAction
import org.nlogo.window.GUIWorkspace
import javax.swing.JPanel
import uk.ac.surrey.soc.cress.extrawidgets.api.ExtraWidget
import uk.ac.surrey.soc.cress.extrawidgets.api.Kind
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyDef
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyMap
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyValue
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKey
import uk.ac.surrey.soc.cress.extrawidgets.api.XWException

class TabKind extends Kind[Tab] {

  val name = "TAB"

  object Title extends PropertyDef[Tab] {
    val valueType = StringType
    def set(w: Tab, newValue: PropertyValue, oldValue: Option[PropertyValue]): Unit =
      w.setTitle(newValue.toString)
    def unset(w: Tab) = w.setTitle(w.key)
  }

  override val propertyDefs = Map(
    "TITLE" -> Title
  )

  def newInstance(key: WidgetKey, properties: PropertyMap, ws: GUIWorkspace) = {
    ws.getFrame.asInstanceOf[AppFrame].getLinkChildren.collectFirst {
      case app: App ⇒ new Tab(this, key, properties, app.tabs)
    }.getOrElse(throw new XWException("Tab widget can't access application tabs."))
  }

}

class Tab(
  val kind: Kind[Tab],
  val key: WidgetKey,
  properties: PropertyMap,
  tabs: Tabs)
  extends JPanel
  with ExtraWidget[Tab] {

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
      // TODO: modify title in menu
    }
}
