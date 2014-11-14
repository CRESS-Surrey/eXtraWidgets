package uk.ac.surrey.xw.api

import java.awt.Component
import java.awt.Container

trait ControlsChildrenEnabling extends Component {
  abstract override def setEnabled(b: Boolean) {
    super.setEnabled(b)
    this match {
      case container: Container ⇒
        container.getComponents.foreach(_.setEnabled(b))
    }
  }
}
