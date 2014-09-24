package uk.ac.surrey.soc.cress.extrawidgets.plugin.gui

import java.awt.Component
import java.awt.Container

import scala.Option.option2Iterable
import scala.collection.TraversableOnce.flattenTraversableOnce

import org.nlogo.app.Tabs
import org.nlogo.app.ToolsMenu

import Strings.CreateTab
import Strings.DefaultTabName
import Strings.InvalidTabName
import Strings.TabNameQuestion
import uk.ac.surrey.soc.cress.extrawidgets.plugin.controller.Controller
import uk.ac.surrey.soc.cress.extrawidgets.plugin.model._
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
    println("Removing widget " + widget + " (not implemented)")
  }

  def createWidget(propertyMap: PropertyMap): Unit = {
    println("Creating widget from " + propertyMap + " (not implemented)")
  }

  def updateWidget(propertyMap: PropertyMap): Unit = {
    println("Updating widget from " + propertyMap + " (not implemented)")
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
