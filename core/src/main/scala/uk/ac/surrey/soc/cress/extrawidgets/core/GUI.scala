package uk.ac.surrey.soc.cress.extrawidgets.core

import java.awt.Component
import java.awt.Container

import scala.Array.canBuildFrom
import scala.Option.option2Iterable
import scala.collection.TraversableOnce.flattenTraversableOnce

import org.nlogo.app.Tabs
import org.nlogo.app.ToolsMenu
import org.nlogo.swing.RichAction

import Strings.CreateTab
import Strings.DefaultTabName
import Strings.TabIDQuestion
import Swing.inputDialog
import Swing.warningDialog
import uk.ac.surrey.soc.cress.extrawidgets.api.ExtraWidget
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyMap
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKey
import uk.ac.surrey.soc.cress.extrawidgets.state.Writer

class GUI(val tabs: Tabs, val toolsMenu: ToolsMenu, val writer: Writer) {

  toolsMenu.addSeparator()
  toolsMenu.addMenuItem(CreateTab, 'X', true, () ⇒ createNewTab())

  def makeWidgetsMap: Map[WidgetKey, ExtraWidget] = {
    val ts = getWidgetsIn(tabs)
    val ws = ts ++ ts.collect { case t: Container ⇒ t }.flatMap(getWidgetsIn)
    ws.map(w ⇒ w.key -> w)(collection.breakOut)
  }

  private def getWidgetsIn(container: Container) =
    container.getComponents.collect {
      case w: ExtraWidget ⇒ w
    }

  def removeWidget(widget: ExtraWidget): Unit = {
    println("Removing widget " + widget.key)
    widget match {
      case tab: ExtraWidgetsTab ⇒ removeTab(tab)
    }
  }

  def createWidget(widgetKey: WidgetKey, propertyMap: PropertyMap): Unit = {
    println("Creating widget from " + (widgetKey, propertyMap))
    propertyMap.get("kind") match {
      case Some("tab") ⇒ {
        val label = propertyMap.getOrElse("label", widgetKey).toString
        val tab = new ExtraWidgetsTab(widgetKey, label)
        tabs.addTab(label, tab)
        val i = tabs.tabsMenu.getItemCount
        tabs.tabsMenu.addMenuItem(label, ('1' + i).toChar,
          RichAction { _ ⇒ tabs.setSelectedIndex(i) })
      }
      case Some(kind) ⇒ warningDialog("Unknown widget kind for " + widgetKey + ":" + kind + "!")
      case None ⇒ warningDialog("No widget kind specified for " + widgetKey + "!")
    }
  }

  def updateWidget(widgetKey: WidgetKey, propertyMap: PropertyMap): Unit = {
    println("Updating widget from " + (widgetKey, propertyMap) + " (not implemented)")
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
    def askName(default: String) = inputDialog(TabIDQuestion, default)
    Iterator
      .iterate(askName(DefaultTabName))(_.flatMap(askName))
      .takeWhile(_.isDefined)
      .flatten
      .map(addTab)
      .takeWhile(_.isLeft)
      .flatMap(_.left.toSeq)
      .foreach(warningDialog)
  }

  def addTab(id: String): Either[String, Unit] = {
    writer.add("tab", id)
  }

  def removeTab(id: String): Unit = {
    writer.remove(id)
  }

}
