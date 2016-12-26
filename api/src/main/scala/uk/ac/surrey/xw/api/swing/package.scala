package uk.ac.surrey.xw.api

import java.awt.Component
import java.awt.Container
import java.awt.ItemSelectable
import java.awt.event.ActionEvent
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.awt.event.ItemEvent
import java.awt.event.ItemListener

import javax.swing.AbstractAction
import javax.swing.AbstractButton
import javax.swing.JList
import javax.swing.JSlider
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener

import scala.language.implicitConversions

import org.nlogo.core.I18N

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

  def listSelectionListener[T](f: (ListSelectionEvent) ⇒ T) =
    new ListSelectionListener {
      override def valueChanged(e: ListSelectionEvent) = f(e)
    }
  implicit def enrichJList[T](jl: JList[T]) = new RichJList(jl)
  class RichJList[T](jl: JList[T]) {
    def onValueChanged[T](f: (ListSelectionEvent) ⇒ T) =
      jl.addListSelectionListener(listSelectionListener(f))
  }

  def newAction[T](f: (ActionEvent) ⇒ T) = new AbstractAction {
    def actionPerformed(evt: ActionEvent) = f(evt)
  }

  implicit def enrichAbstractButton(b: AbstractButton) = new RichAbstractButton(b)
  class RichAbstractButton(b: AbstractButton) {
    def onActionPerformed[T](f: (ActionEvent) ⇒ T) =
      b.addActionListener(newAction(f))
  }

  implicit def enrichComponent(c: Component) = new RichComponent(c)
  class RichComponent(c: Component) {
    def onFocusGained[T](f: (FocusEvent) ⇒ T) =
      c.addFocusListener(new FocusListener {
        override def focusGained(evt: FocusEvent) = f(evt)
        override def focusLost(evt: FocusEvent) = Unit
      })
    def onFocusLost[T](f: (FocusEvent) ⇒ T) =
      c.addFocusListener(new FocusListener {
        override def focusGained(evt: FocusEvent) = Unit
        override def focusLost(evt: FocusEvent) = f(evt)
      })
    def showMessage(msg: String) {
      val frame = org.nlogo.awt.Hierarchy.getFrame(c)
      if (frame != null) {
        org.nlogo.swing.OptionDialog.showMessage(frame, msg,
          msg, Array(I18N.gui.get("common.buttons.ok")))
      }
    }
    def allChildren: Seq[Component] =
      c match {
        case c: Container ⇒
          c.getComponents ++ c.getComponents.flatMap(_.allChildren)
        case _ ⇒ Seq()
      }
  }

}
