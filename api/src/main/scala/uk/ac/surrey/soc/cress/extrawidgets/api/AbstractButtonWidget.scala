package uk.ac.surrey.soc.cress.extrawidgets.api

import javax.swing.AbstractButton
import uk.ac.surrey.soc.cress.extrawidgets.api.swing.enrichItemSelectable

trait AbstractButtonWidget extends JComponentWidget {
  self: AbstractButton ⇒
  val xwSelected = new BooleanPropertyDef(this, (b) ⇒ setSelected(b), isSelected)
  val xwText = new StringPropertyDef(this, setText, getText)

  self.onItemStateChanged { _ ⇒
    xwSelected.setValue(Boolean.box(isSelected))
  }
}
