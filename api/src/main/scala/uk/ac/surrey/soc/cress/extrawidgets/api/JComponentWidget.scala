package uk.ac.surrey.soc.cress.extrawidgets.api

import javax.swing.JComponent

trait JComponentWidget extends ComponentWidget {
  self: JComponent ⇒
  val xwOpaque = new BooleanPropertyDef(this, (b) ⇒ setOpaque(b), isOpaque, const(isOpaque))
}
