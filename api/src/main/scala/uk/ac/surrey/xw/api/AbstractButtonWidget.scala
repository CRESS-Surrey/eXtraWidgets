package uk.ac.surrey.xw.api

import javax.swing.AbstractButton

import uk.ac.surrey.xw.api.swing.enrichItemSelectable

abstract class AbstractButtonWidgetKind[W <: AbstractButtonWidget]
  extends JComponentWidgetKind[W] {
  val selectedProperty =
    new BooleanProperty[W]("SELECTED", Some(_.setSelected(_)), _.isSelected)
  override def propertySet = super.propertySet ++ Set(
    selectedProperty,
    new StringProperty[W]("LABEL", Some(_.setText(_)), _.getText))
}

trait AbstractButtonWidget extends AbstractButton with JComponentWidget {
  override val kind: AbstractButtonWidgetKind[this.type]

  this.onItemStateChanged { _ ⇒
    updateInState(kind.selectedProperty)
  }
}
