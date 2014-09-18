package uk.ac.surrey.soc.cress.extrawidgets.plugin

import java.awt.Component
import org.nlogo.app.Tabs
import sun.reflect.generics.reflectiveObjects.NotImplementedException

class TabsManager(tabs: Tabs) {

  def removeTab(component: Component): Unit =
    (0 until tabs.getTabCount)
      .find(i ⇒ tabs.getComponentAt(i) == component)
      .foreach { i ⇒
        tabs.remove(component)
        tabs.removeMenuItem(i)
      }

  def addTab(component: Component) = throw new NotImplementedException
}
