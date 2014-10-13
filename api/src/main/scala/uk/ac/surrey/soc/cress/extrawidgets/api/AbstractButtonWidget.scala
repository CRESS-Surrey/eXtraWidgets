package uk.ac.surrey.soc.cress.extrawidgets.api

import javax.swing.AbstractButton

trait AbstractButtonWidget extends JComponentWidget {
  self: AbstractButton ⇒
  val xwValue = new BooleanPropertyDef(this, (b) ⇒ setSelected(b), isSelected, const(isSelected))
  val xwText = new StringPropertyDef(this, setText, getText, const(getText))
}
