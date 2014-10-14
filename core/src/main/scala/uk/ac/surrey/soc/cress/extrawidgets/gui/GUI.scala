package uk.ac.surrey.soc.cress.extrawidgets.gui

import java.awt.Component
import java.awt.Container
import java.awt.Toolkit.getDefaultToolkit
import java.awt.event.InputEvent.SHIFT_MASK
import scala.Array.canBuildFrom
import scala.Option.option2Iterable
import scala.collection.TraversableOnce.flattenTraversableOnce
import org.nlogo.app.App
import org.nlogo.app.ToolsMenu
import org.nlogo.swing.RichAction
import Strings.DefaultTabName
import Strings.TabIDQuestion
import Swing.inputDialog
import Swing.warningDialog
import javax.swing.JMenuItem
import javax.swing.KeyStroke.getKeyStroke
import uk.ac.surrey.soc.cress.extrawidgets.api.ExtraWidget
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyMap
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKey
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKind
import uk.ac.surrey.soc.cress.extrawidgets.api.XWException
import uk.ac.surrey.soc.cress.extrawidgets.api.enrichOption
import uk.ac.surrey.soc.cress.extrawidgets.api.makeKey
import uk.ac.surrey.soc.cress.extrawidgets.api.normalizeKey
import uk.ac.surrey.soc.cress.extrawidgets.api.tryTo
import uk.ac.surrey.soc.cress.extrawidgets.state.Writer
import uk.ac.surrey.soc.cress.extrawidgets.state.Reader

class CreateTabMenuItem(val gui: GUI)
  extends JMenuItem(RichAction(Strings.CreateTab)(_ ⇒ gui.createNewTab())) {
  setIcon(null)
  setAccelerator(getKeyStroke(
    'X', SHIFT_MASK | getDefaultToolkit.getMenuShortcutKeyMask))
}

class GUI(
  val app: App,
  val reader: Reader, // needed for tests
  val writer: Writer,
  val widgetKinds: Map[String, WidgetKind]) {

  val tabs = app.tabs
  val tabKindName = makeKey(classOf[Tab].getSimpleName)

  val toolsMenu = {
    val menuBar = app.frame.getJMenuBar
    (0 until menuBar.getMenuCount)
      .map(menuBar.getMenu)
      .collectFirst {
        case toolsMenu: ToolsMenu ⇒ toolsMenu
      }
      .getOrElse(throw new XWException("Can't find Tools menu."))
  }

  toolsMenu.addSeparator()
  toolsMenu.add(new CreateTabMenuItem(this))

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
      case w ⇒ for (tab ← getTabFor(widget.key, widget.propertyMap).right) {
        tab.remove(widget)
        tab.validate()
      }
    }
  }

  def createWidget(widgetKey: WidgetKey, propertyMap: PropertyMap): Either[XWException, Unit] = {
    println("Creating widget from " + (widgetKey, propertyMap))
    for {
      kindName ← propertyMap.get("KIND").map(_.toString).orException(
        "Can't find KIND for " + widgetKey + " in " + propertyMap).right
      kind ← widgetKinds.get(normalizeKey(kindName)).orException(
        "Kind " + kindName + " not loaded.").right
    } yield {
      def createWidget = kind.newInstance(widgetKey, propertyMap, app.workspace)
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

  def getTabFor(widgetKey: WidgetKey, propertyMap: PropertyMap): Either[XWException, Tab] = {
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
      .map(writer.add(_, Map("kind" -> tabKindName)))
      .takeWhile(_.isLeft)
      .flatMap(_.left.toSeq)
      .foreach(warningDialog)
  }
}
