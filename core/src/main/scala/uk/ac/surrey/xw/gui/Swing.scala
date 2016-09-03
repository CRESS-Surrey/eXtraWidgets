package uk.ac.surrey.xw.gui

import java.awt.Component
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent

import javax.swing.JComponent
import javax.swing.JOptionPane
import javax.swing.event.AncestorEvent
import javax.swing.event.AncestorListener

import scala.language.implicitConversions

import uk.ac.surrey.xw.api.XWException

object Swing {

  implicit def enrichComponent(component: Component) = new RichComponent(component)
  implicit def enrichJComponent(jComponent: JComponent) = new RichJComponent(jComponent)

  class RichComponent(component: Component) {
    def onComponentShown[T](f: ComponentEvent ⇒ T): Unit = {
      component.addComponentListener(new ComponentAdapter() {
        override def componentShown(e: ComponentEvent): Unit = f(e)
      })
    }
    def onComponentHidden[T](f: ComponentEvent ⇒ T): Unit = {
      component.addComponentListener(new ComponentAdapter() {
        override def componentHidden(e: ComponentEvent): Unit = f(e)
      })
    }
    def onComponentMoved[T](f: ComponentEvent ⇒ T): Unit = {
      component.addComponentListener(new ComponentAdapter() {
        override def componentMoved(e: ComponentEvent): Unit = f(e)
      })
    }
    def onComponentResized[T](f: ComponentEvent ⇒ T): Unit = {
      component.addComponentListener(new ComponentAdapter() {
        override def componentResized(e: ComponentEvent): Unit = f(e)
      })
    }

  }

  class RichJComponent(component: JComponent) extends RichComponent(component) {

    trait AncestorAdapter extends AncestorListener {
      override def ancestorAdded(e: AncestorEvent) = Unit
      override def ancestorMoved(e: AncestorEvent) = Unit
      override def ancestorRemoved(e: AncestorEvent) = Unit
    }

    def onAncestorAdded[T](f: AncestorEvent ⇒ T) {
      component.addAncestorListener(new AncestorAdapter() {
        override def ancestorAdded(e: AncestorEvent) = f(e)
      })
    }
    def onAncestorMoved[T](f: AncestorEvent ⇒ T) {
      component.addAncestorListener(new AncestorAdapter() {
        override def ancestorMoved(e: AncestorEvent) = f(e)
      })
    }
    def onAncestorRemoved[T](f: AncestorEvent ⇒ T) {
      component.addAncestorListener(new AncestorAdapter() {
        override def ancestorRemoved(e: AncestorEvent) = f(e)
      })
    }
  }

  def inputDialog(question: String, default: String): Option[String] = {
    Option(JOptionPane.showInputDialog(
      null, // parent frame
      question,
      "eXtraWidgets",
      JOptionPane.QUESTION_MESSAGE,
      null, // icon
      null, // options
      default))
      .collect { case s: String ⇒ s }
      .map(_.trim)
  }

  def warningDialog(exception: XWException): Unit = {
    JOptionPane.showMessageDialog(
      null, // parent frame
      exception.message,
      "eXtraWidgets Extension Warning!",
      JOptionPane.WARNING_MESSAGE)
  }
}
