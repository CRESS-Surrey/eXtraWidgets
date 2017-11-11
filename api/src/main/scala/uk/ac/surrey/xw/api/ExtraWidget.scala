package uk.ac.surrey.xw.api

import java.awt.Color.gray
import java.awt.Component

import org.nlogo.window.GUIWorkspace

import uk.ac.surrey.xw.api.swing.enrichComponent

trait ExtraWidget extends Component {

  val ws: GUIWorkspace
  val key: WidgetKey
  val state: State

  val kind: WidgetKind[this.type]

  def init(propertyMap: PropertyMap): Unit =
    propertyMap.toSeq
      // make sure the default property gets set last
      // (see https://github.com/CRESS-Surrey/eXtraWidgets/issues/162)
      .sortBy  { case (k, v) ⇒ kind.defaultProperty.map(_.key).contains(k) }
      .foreach { case (k, v) ⇒ setProperty(k, v) }

  def setProperty(
    propertyKey: PropertyKey,
    propertyValue: PropertyValue): Unit =
    for (property ← kind.properties.get(propertyKey))
      property.set(this, propertyValue)

  def updateInState(property: Property[_, this.type]): Unit =
    state.set(property.key, this.key, property.get(this))

  override def setEnabled(b: Boolean) = {
    super.setEnabled(b)
    Some(this).collect {
      case comp: ComponentWidget ⇒
        comp.setForeground(if (b) comp.getFontColor else gray)
    }
    this.allChildren.foreach(_.setEnabled(b))
  }
}
