package uk.ac.surrey.soc.cress.extrawidgets.core

import java.awt.Component
import java.awt.Container

import scala.Array.canBuildFrom
import scala.Option.option2Iterable
import scala.collection.TraversableOnce.flattenTraversableOnce

import org.nlogo.app.App
import org.nlogo.app.ToolsMenu

import Strings.CreateTab
import Strings.DefaultTabName
import Strings.TabIDQuestion
import Swing.inputDialog
import Swing.warningDialog
import uk.ac.surrey.soc.cress.extrawidgets.api.ExtraWidget
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyMap
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKey
import uk.ac.surrey.soc.cress.extrawidgets.state.Writer

class GUI(val app: App, val toolsMenu: ToolsMenu, val writer: Writer) {

  val tabs = app.tabs

  toolsMenu.addSeparator()
  toolsMenu.addMenuItem(CreateTab, 'X', true, () ⇒ createNewTab())

  def makeWidgetsMap: Map[WidgetKey, ExtraWidget[_]] = {
    val ts = getWidgetsIn(tabs)
    val ws = ts ++ ts.collect { case t: Container ⇒ t }.flatMap(getWidgetsIn)
    ws.map(w ⇒ w.key -> w).toMap
  }

  private def getWidgetsIn(container: Container) =
    container.getComponents.collect {
      case w: ExtraWidget[_] ⇒ w
    }

  def removeWidget(widget: ExtraWidget[_]): Unit = {
    println("Removing widget " + widget.key)
    widget match {
      case tab: Tab ⇒ removeTab(tab)
    }
  }

  val tabKind = new TabKind // should this be loaded with other widgets?
  def createWidget(widgetKey: WidgetKey, propertyMap: PropertyMap): Unit = {
    println("Creating widget from " + (widgetKey, propertyMap))
    propertyMap.get("KIND") match {
      case Some("tab") ⇒ tabKind.newInstance(widgetKey, propertyMap, app.workspace)
      case Some(kind) ⇒ warningDialog("Unknown widget kind for " + widgetKey + ":" + kind + "!")
      case None ⇒ warningDialog("No widget kind specified for " + widgetKey + "!")
    }
  }

  def updateWidget(widget: ExtraWidget[_], propertyMap: PropertyMap): Unit = {
    println("Updating widget from " + propertyMap)
    widget.update(propertyMap)
  }

  def removeTab(component: Component): Unit =
    (0 until tabs.getTabCount)
      .find(i ⇒ tabs.getComponentAt(i) == component)
      .foreach { i ⇒
        tabs.remove(component)
        tabs.removeMenuItem(i)
      }

  def extraWidgetTabs: Vector[Tab] =
    tabs.getComponents.collect {
      case t: Tab ⇒ t
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
    writer.add(id, Map("kind" -> "tab"))
  }

  def removeTab(id: String): Unit = {
    writer.remove(id)
  }

}
