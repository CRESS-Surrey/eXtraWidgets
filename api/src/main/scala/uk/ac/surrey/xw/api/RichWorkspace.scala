package uk.ac.surrey.xw.api

import scala.language.implicitConversions

import org.nlogo.app.App
import org.nlogo.app.AppFrame
import org.nlogo.app.TabManager
import org.nlogo.app.TabsPanel
import org.nlogo.window.GUIWorkspace

object RichWorkspace {
  implicit def enrichWorkspace(ws: GUIWorkspace): RichWorkspace =
    new RichWorkspace(ws)
}

class RichWorkspace(ws: GUIWorkspace) {

  def app: App = ws.getFrame.asInstanceOf[AppFrame].getLinkChildren
    .collectFirst { case app: App ⇒ app }
    .getOrElse(throw new XWException("Can't access application tabs."))

  def tabManager: TabManager = app.tabManager

  def tabs: TabsPanel = tabManager.mainTabs

  def xwTabs: Seq[Tab] = tabs.getComponents.collect { case t: Tab ⇒ t }.toSeq

  def reorderTabs(state: State): Unit =
    for (tab ← xwTabs.sortBy(t ⇒ (t.getOrder, state.tabCreationOrder(t.key)))) {
      tab.removeFromAppTabs()
      tab.addToAppTabs()
    }
}
