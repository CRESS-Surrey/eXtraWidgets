package uk.ac.surrey.soc.cress.extrawidgets.api

import javax.swing.AbstractButton
import uk.ac.surrey.soc.cress.extrawidgets.api.swing.enrichItemSelectable

trait AbstractButtonWidget extends JComponentWidget {
  self: AbstractButton ⇒
  val xwSelected = new BooleanPropertyDef(setSelected(_), isSelected)
  val xwLabel = new StringPropertyDef(setText, getText)

  self.onItemStateChanged { _ ⇒
    updateInState(xwSelected)
  }
}
