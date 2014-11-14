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

  def xwTabs: IndexedSeq[Tab] = {
    val _tabs = tabs
    (0 until _tabs.getTabCount)
      .map(_tabs.getComponentAt)
      .collect { case tab: Tab ⇒ tab }
  }
}
