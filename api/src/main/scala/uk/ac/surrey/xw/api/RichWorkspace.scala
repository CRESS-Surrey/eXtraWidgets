package uk.ac.surrey.xw.api

import org.nlogo.app.App
import org.nlogo.app.AppFrame
import org.nlogo.app.Tabs
import org.nlogo.window.GUIWorkspace

object RichWorkspace {
  implicit def enrichWorkspace(ws: GUIWorkspace) =
    new RichWorkspace(ws)
}

class RichWorkspace(ws: GUIWorkspace) {

  def tabs: Tabs = ws.getFrame.asInstanceOf[AppFrame].getLinkChildren
    .collectFirst { case app: App ⇒ app.tabs }
    .getOrElse(throw new XWException("Can't access application tabs."))

  def xwTabs = tabs.getComponents.collect { case t: Tab ⇒ t }

  def reorderTabs(state: State): Unit =
    for (tab ← xwTabs.sortBy(t ⇒ (t.getOrder, state.tabCreationOrder(t.key)))) {
      tabs.remove(tab)
      tab.addToAppTabs()
    }
}
