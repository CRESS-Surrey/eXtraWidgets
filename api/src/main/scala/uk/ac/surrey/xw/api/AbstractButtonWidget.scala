package uk.ac.surrey.xw.api

import javax.swing.AbstractButton
import uk.ac.surrey.xw.api.swing.enrichItemSelectable

abstract class AbstractButtonWidgetKind[W <: AbstractButtonWidget with AbstractButton]
  extends JComponentWidgetKind[W] {
  val selectedProperty =
    new BooleanProperty[W]("SELECTED", _.setSelected(_), _.isSelected)
  override def propertySet = super.propertySet ++ Set(
    selectedProperty,
    new StringProperty[W]("LABEL", _.setText(_), _.getText))
}

trait AbstractButtonWidget extends JComponentWidget {
  self: AbstractButton ⇒
  override val kind: AbstractButtonWidgetKind[this.type]

  self.onItemStateChanged { _ ⇒
    updateInState(kind.selectedProperty)
  }
}
