package uk.ac.surrey.soc.cress.extrawidgets.plugin.gui

import java.awt.Component
import java.awt.Container

import scala.Array.canBuildFrom
import scala.Option.option2Iterable
import scala.collection.TraversableOnce.flattenTraversableOnce

import org.nlogo.api.I18N
import org.nlogo.app.Tabs
import org.nlogo.app.ToolsMenu
import org.nlogo.swing.TabsMenu

import Strings.CreateTab
import Strings.DefaultTabName
import Strings.InvalidTabName
import Strings.TabIDQuestion
import uk.ac.surrey.soc.cress.extrawidgets.plugin.controller.Controller
import uk.ac.surrey.soc.cress.extrawidgets.plugin.model.PropertyMap
import uk.ac.surrey.soc.cress.extrawidgets.plugin.model.WidgetID
import uk.ac.surrey.soc.cress.extrawidgets.plugin.util.Swing.inputDialog
import uk.ac.surrey.soc.cress.extrawidgets.plugin.util.Swing.warningDialog
import uk.ac.surrey.soc.cress.extrawidgets.plugin.view.ExtraWidgetsTab

class GUI(val tabs: Tabs, val toolsMenu: ToolsMenu, val controller: Controller) {

  toolsMenu.addSeparator()
  toolsMenu.addMenuItem(CreateTab, 'X', true, () ⇒ createNewTab())

  def makeWidgetsMap: Map[WidgetID, ExtraWidget] = {
    val ts = getWidgetsIn(tabs)
    val ws = ts ++ ts.collect { case t: Container ⇒ t }.flatMap(getWidgetsIn)
    ws.map(w ⇒ w.id -> w)(collection.breakOut)
  }

  private def getWidgetsIn(container: Container) =
    container.getComponents.collect {
      case w: ExtraWidget ⇒ w
    }

  def removeWidget(widget: ExtraWidget): Unit = {
    println("Removing widget " + widget.id + " (not implemented)")
  }

  def createWidget(id: WidgetID, propertyMap: PropertyMap): Unit = {
    println("Creating widget from " + (id, propertyMap))
    propertyMap.get("kind") match {
      case Some("tab") ⇒ {
        val label = propertyMap.getOrElse("label", id).toString
        val tab = new ExtraWidgetsTab(id, label)
        tabs.addTab(label, tab)
        tabs.tabsMenu = new TabsMenu(I18N.gui.get("menu.tabs"), tabs)
      }
      case _ ⇒ warningDialog("Error", "Unknown widget kind!")
    }
  }

  def updateWidget(id: WidgetID, propertyMap: PropertyMap): Unit = {
    println("Updating widget from " + (id, propertyMap) + " (not implemented)")
  }

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
      TabIDQuestion, default)
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
