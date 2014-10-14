package uk.ac.surrey.soc.cress.extrawidgets.gui

import java.awt.Component
import java.awt.Container

import scala.Array.canBuildFrom
import scala.Option.option2Iterable
import scala.collection.TraversableOnce.flattenTraversableOnce
import scala.collection.mutable.Publisher
import scala.collection.mutable.Subscriber

import org.nlogo.app.App
import org.nlogo.awt.EventQueue.invokeLater

import Strings.DefaultTabName
import Strings.TabIDQuestion
import Swing.inputDialog
import Swing.warningDialog
import javax.swing.SwingUtilities.getAncestorOfClass
import uk.ac.surrey.soc.cress.extrawidgets.api.ExtraWidget
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyKey
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyMap
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyValue
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKey
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKind
import uk.ac.surrey.soc.cress.extrawidgets.api.XWException
import uk.ac.surrey.soc.cress.extrawidgets.api.enrichOption
import uk.ac.surrey.soc.cress.extrawidgets.api.makeKey
import uk.ac.surrey.soc.cress.extrawidgets.api.normalizeKey
import uk.ac.surrey.soc.cress.extrawidgets.api.toRunnable
import uk.ac.surrey.soc.cress.extrawidgets.api.tryTo
import uk.ac.surrey.soc.cress.extrawidgets.state.AddWidget
import uk.ac.surrey.soc.cress.extrawidgets.state.RemoveWidget
import uk.ac.surrey.soc.cress.extrawidgets.state.SetProperty
import uk.ac.surrey.soc.cress.extrawidgets.state.StateEvent
import uk.ac.surrey.soc.cress.extrawidgets.state.Writer

class GUI(
  val app: App,
  val writer: Writer,
  val widgetKinds: Map[String, WidgetKind])
  extends Subscriber[StateEvent, Publisher[StateEvent]] {

  writer.subscribe(this)

  val tabs = app.tabs
  val tabKindName = makeKey(classOf[Tab].getSimpleName)

  override def notify(pub: Publisher[StateEvent], event: StateEvent): Unit =
    invokeLater {
      event match {
        case AddWidget(widgetKey, propertyMap) ⇒
          addWidget(widgetKey, propertyMap)
        case SetProperty(widgetKey, propertyKey, propertyValue) ⇒
          setProperty(widgetKey, propertyKey, propertyValue)
        case RemoveWidget(widgetKey) ⇒
          removeWidget(widgetKey)
      }
    }

  def getWidget(widgetKey: WidgetKey): Option[ExtraWidget] = {
    def getWidgetsIn(container: Container) =
      container.getComponents.collect {
        case w: ExtraWidget ⇒ w
      }
    val extraTabs = getWidgetsIn(tabs)
    extraTabs
      .find(_.key == widgetKey)
      .orElse {
        extraTabs
          .collect { case t: Container ⇒ t }
          .flatMap(getWidgetsIn)
          .find(_.key == widgetKey)
      }
  }

  private def addWidget(widgetKey: WidgetKey, propertyMap: PropertyMap): Either[XWException, Unit] = {
    println("Creating widget from " + (widgetKey, propertyMap))
    for {
      kindName ← propertyMap.get("KIND").map(_.toString).orException(
        "Can't find KIND for " + widgetKey + " in " + propertyMap).right
      kind ← widgetKinds.get(normalizeKey(kindName)).orException(
        "Kind " + kindName + " not loaded.").right
    } yield {
      def createWidget = {
        val w = kind.newInstance(widgetKey, propertyMap, app.workspace)
        w.init(propertyMap)
        w
      }
      if (kind.name == tabKindName)
        tryTo(createWidget)
      else
        for {
          tab ← getTabFor(widgetKey, propertyMap).right
          widget ← tryTo(createWidget).right
        } yield {
          tab.add(widget)
          tab.validate()
        }
    }
  }

  private def setProperty(
    widgetKey: WidgetKey,
    propertyKey: PropertyKey,
    propertyValue: PropertyValue): Unit = {
    getWidget(widgetKey).foreach(_.setProperty(propertyKey, propertyValue))
  }

  private def removeWidget(widgetKey: WidgetKey): Unit = {
    println("Removing widget " + widgetKey)
    for (w ← getWidget(widgetKey)) w match {
      case tab: Tab ⇒ removeTab(tab)
      case _ ⇒
        for (container ← Option(getAncestorOfClass(classOf[Tab], w))) {
          container.remove(w)
          container.validate()
        }
    }
  }

  private def getTabFor(widgetKey: WidgetKey, propertyMap: PropertyMap): Either[XWException, Tab] = {
    val tabs = extraWidgetTabs
    for {
      tabKey ← propertyMap
        .get(tabKindName)
        .map(key ⇒ normalizeKey(key.toString))
        .orElse(tabs.headOption.map(_.key))
        .orException("There exists no tab for widget " + widgetKey + ".").right
      tab ← tabs
        .find(_.key == tabKey)
        .orException("Tab " + tabKey + " does not exist for widget " + widgetKey + ".").right
    } yield tab
  }

  private def removeTab(component: Component): Unit =
    (0 until tabs.getTabCount)
      .find(i ⇒ tabs.getComponentAt(i) == component)
      .foreach { i ⇒
        tabs.remove(component)
        tabs.removeMenuItem(i)
      }

  private def extraWidgetTabs: Vector[Tab] =
    tabs.getComponents.collect {
      case t: Tab ⇒ t
    }(collection.breakOut)

  def createNewTab(): Unit = {
    def askName(default: String) = inputDialog(TabIDQuestion, default)
    Iterator
      .iterate(askName(DefaultTabName))(_.flatMap(askName))
      .takeWhile(_.isDefined)
      .flatten
      .map(writer.add(_, Map("kind" -> tabKindName)))
      .takeWhile(_.isLeft)
      .flatMap(_.left.toSeq)
      .foreach(warningDialog)
  }
}
