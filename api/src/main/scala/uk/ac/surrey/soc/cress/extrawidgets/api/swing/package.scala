package uk.ac.surrey.soc.cress.extrawidgets.api

import javax.swing.JSlider
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

package object swing {

  def changeListener[T](f: (ChangeEvent) ⇒ T) = new ChangeListener {
    override def stateChanged(e: ChangeEvent) = f(e)
  }

  implicit def enrichSlider(s: JSlider) = new RichJSlider(s: JSlider)
  class RichJSlider(s: JSlider) {
    def onChange[T](f: (ChangeEvent) ⇒ T) =
      s.addChangeListener(changeListener(f))
  }

}
