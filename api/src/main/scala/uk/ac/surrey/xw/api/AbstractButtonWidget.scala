package uk.ac.surrey.xw.api

import javax.swing.AbstractButton
import uk.ac.surrey.xw.api.swing.enrichItemSelectable

trait AbstractButtonWidget extends JComponentWidget {
  self: AbstractButton ⇒
  val xwSelected = new BooleanProperty(setSelected(_), isSelected)
  val xwLabel = new StringProperty(setText, getText)

  self.onItemStateChanged { _ ⇒
    updateInState(xwSelected)
  }
}
