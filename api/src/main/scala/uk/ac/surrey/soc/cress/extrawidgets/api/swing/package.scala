package uk.ac.surrey.soc.cress.extrawidgets.api

import javax.swing.JSlider
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener
import java.awt.event.ItemEvent
import java.awt.ItemSelectable
import java.awt.event.ItemListener

package object swing {

  def changeListener[T](f: (ChangeEvent) ⇒ T) = new ChangeListener {
    override def stateChanged(e: ChangeEvent) = f(e)
  }

  implicit def enrichSlider(s: JSlider) = new RichJSlider(s: JSlider)
  class RichJSlider(s: JSlider) {
    def onStateChange[T](f: (ChangeEvent) ⇒ T) =
      s.addChangeListener(changeListener(f))
  }

  def itemListener[T](f: (ItemEvent) ⇒ T) = new ItemListener {
    override def itemStateChanged(e: ItemEvent) = f(e)
  }

  implicit def enrichItemSelectable(is: ItemSelectable) =
    new RichItemSelectable(is)
  class RichItemSelectable(is: ItemSelectable) {
    def onItemStateChanged[T](f: (ItemEvent) ⇒ T) =
      is.addItemListener(itemListener(f))
  }

}
