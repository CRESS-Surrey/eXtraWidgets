package uk.ac.surrey.soc.cress.extrawidgets.plugin.controller

import java.awt.Component

import scala.Option.option2Iterable
import scala.collection.TraversableOnce.flattenTraversableOnce

import org.nlogo.app.Tabs
import org.nlogo.app.ToolsMenu

import Strings.CreateTab
import Strings.DefaultTabName
import Strings.InvalidTabName
import Strings.TabNameQuestion
import uk.ac.surrey.soc.cress.extrawidgets.plugin.util.Swing.inputDialog
import uk.ac.surrey.soc.cress.extrawidgets.plugin.util.Swing.warningDialog
import uk.ac.surrey.soc.cress.extrawidgets.plugin.view.ExtraWidgetsTab

class TabsManager(val tabs: Tabs, val toolsMenu: ToolsMenu, val controller: Controller) {

  toolsMenu.addSeparator()
  toolsMenu.addMenuItem(CreateTab, 'X', true, () ⇒ createNewTab())

  def removeTab(component: Component): Unit =
    (0 until tabs.getTabCount)
      .find(i ⇒ tabs.getComponentAt(i) == component)
      .foreach { i ⇒
        tabs.remove(component)
        tabs.removeMenuItem(i)
      }

  def extraWidgetTabs: Vector[ExtraWidgetsTab] =
    tabs.getComponents.collect {
      case t: ExtraWidgetsTab ⇒ t
    }(collection.breakOut)

  def createNewTab(): Unit = {
    def askName(default: String) = inputDialog(
      CreateTab,
      TabNameQuestion, default)
    Iterator
      .iterate(askName(DefaultTabName))(_.flatMap(askName))
      .takeWhile(_.isDefined)
      .flatten
      .map(controller.addTab)
      .takeWhile(_.isLeft)
      .flatMap(_.left.toSeq)
      .foreach(warningDialog(InvalidTabName, _))
  }
}
