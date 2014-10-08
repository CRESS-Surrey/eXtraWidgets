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
import uk.ac.surrey.soc.cress.extrawidgets.api.Kind
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyMap
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKey
import uk.ac.surrey.soc.cress.extrawidgets.api.XWException
import uk.ac.surrey.soc.cress.extrawidgets.state.Writer
import uk.ac.surrey.soc.cress.extrawidgets.state.normalizeKey

class GUI(
  val app: App,
  val toolsMenu: ToolsMenu,
  val writer: Writer,
  val widgetKinds: Map[String, Kind]) {

  val tabs = app.tabs

  toolsMenu.addSeparator()
  toolsMenu.addMenuItem(CreateTab, 'X', true, () ⇒ createNewTab())

  def makeWidgetsMap: Map[WidgetKey, ExtraWidget] = {
    val ts = getWidgetsIn(tabs)
    val ws = ts ++ ts.collect { case t: Container ⇒ t }.flatMap(getWidgetsIn)
    ws.map(w ⇒ w.key -> w).toMap
  }

  private def getWidgetsIn(container: Container) =
    container.getComponents.collect {
      case w: ExtraWidget ⇒ w
    }

  def removeWidget(widget: ExtraWidget): Unit = {
    println("Removing widget " + widget.key)
    widget match {
      case tab: Tab ⇒ removeTab(tab)
    }
  }

  def createWidget(widgetKey: WidgetKey, propertyMap: PropertyMap): Unit = {
    println("Creating widget from " + (widgetKey, propertyMap))
    for {
      kindName ← propertyMap.get("KIND").map(_.toString).toRight(new XWException(
        "Can't find KIND for " + widgetKey + " in " + propertyMap)).right
      kind ← widgetKinds.get(normalizeKey(kindName)).toRight(new XWException(
        "Kind " + kindName + " not loaded.")).right
    } kind.newInstance(widgetKey, propertyMap, app.workspace)
  }

  def updateWidget(widget: ExtraWidget, propertyMap: PropertyMap): Unit = {
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

  def addTab(id: String): Either[XWException, Unit] = {
    writer.add(id, Map("kind" -> "tab"))
  }

  def removeTab(id: String): Unit = {
    writer.remove(id)
  }

}
