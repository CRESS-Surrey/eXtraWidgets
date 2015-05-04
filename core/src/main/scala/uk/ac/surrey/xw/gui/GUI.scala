package uk.ac.surrey.xw.gui

import scala.collection.mutable.Publisher
import scala.collection.mutable.Subscriber

import org.nlogo.app.App
import org.nlogo.awt.EventQueue.invokeLater

import uk.ac.surrey.xw.api.ComponentWidget
import uk.ac.surrey.xw.api.ExtraWidget
import uk.ac.surrey.xw.api.PropertyKey
import uk.ac.surrey.xw.api.PropertyMap
import uk.ac.surrey.xw.api.PropertyValue
import uk.ac.surrey.xw.api.RichWorkspace.enrichWorkspace
import uk.ac.surrey.xw.api.Tab
import uk.ac.surrey.xw.api.TabKind
import uk.ac.surrey.xw.api.WidgetKey
import uk.ac.surrey.xw.api.WidgetKind
import uk.ac.surrey.xw.api.enrichOption
import uk.ac.surrey.xw.api.normalizeString
import uk.ac.surrey.xw.api.swing.enrichComponent
import uk.ac.surrey.xw.api.toRunnable
import uk.ac.surrey.xw.state.AddWidget
import uk.ac.surrey.xw.state.RemoveWidget
import uk.ac.surrey.xw.state.SetProperty
import uk.ac.surrey.xw.state.StateEvent
import uk.ac.surrey.xw.state.Writer

class GUI(
  val app: App,
  val writer: Writer,
  val widgetKinds: Map[String, WidgetKind[_]])
  extends Subscriber[StateEvent, Publisher[StateEvent]] {

  writer.subscribe(this, {
    case SetProperty(_,_,_,fromUI) => !fromUI
    case _ => true
  })

  val tabs = app.tabs
  val tabPropertyKey = new TabKind[Tab].name

  override def notify(pub: Publisher[StateEvent], event: StateEvent): Unit =
    invokeLater {
      event match {
        case AddWidget(widgetKey, propertyMap) ⇒
          addWidget(widgetKey, propertyMap)
        case SetProperty(widgetKey, propertyKey, propertyValue, _) ⇒
          setProperty(widgetKey, propertyKey, propertyValue)
        case RemoveWidget(widgetKey) ⇒
          removeWidget(widgetKey)
      }
    }

  def getWidget(widgetKey: WidgetKey): Option[ExtraWidget] = {
    val xwTabs = app.workspace.xwTabs
    (xwTabs ++ xwTabs.flatMap(_.allChildren))
      .collectFirst {
        case w: ExtraWidget if w.key == widgetKey ⇒ w
      }
  }

  private def addWidget(widgetKey: WidgetKey, propertyMap: PropertyMap): Unit =
    for {
      kindName ← propertyMap.get("KIND").map(_.toString).orException(
        "Can't find KIND for " + widgetKey + " in " + propertyMap).right
      kind ← widgetKinds.get(normalizeString(kindName)).orException(
        "Kind " + kindName + " not loaded.").right
    } kind.newWidget(widgetKey, writer, app.workspace).init(propertyMap)

  private def setProperty(
    widgetKey: WidgetKey,
    propertyKey: PropertyKey,
    propertyValue: PropertyValue): Unit =
    getWidget(widgetKey).foreach(
      _.setProperty(propertyKey, propertyValue)
    )

  private def removeWidget(widgetKey: WidgetKey): Unit =
    for (w ← getWidget(widgetKey)) w match {
      case tab: Tab ⇒ tab.removeFromAppTabs()
      case cw: ComponentWidget ⇒
        for (t ← cw.tab) {
          t.panel.remove(cw)
          t.panel.repaint()
        }
    }

}
