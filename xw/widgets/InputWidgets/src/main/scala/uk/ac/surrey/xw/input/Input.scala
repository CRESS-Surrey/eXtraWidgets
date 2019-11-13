package uk.ac.surrey.xw.input

import java.awt.BorderLayout.CENTER
import java.awt.event.KeyEvent.VK_ENTER
import java.awt.event.KeyEvent.VK_ESCAPE

import javax.swing.JTextField
import javax.swing.KeyStroke.getKeyStroke

import scala.Left
import scala.Right

import org.nlogo.api.Dump
import org.nlogo.awt.EventQueue.invokeLater
import org.nlogo.core.NumberParser
import org.nlogo.window.GUIWorkspace

import uk.ac.surrey.xw.api.DoubleProperty
import uk.ac.surrey.xw.api.LabeledPanelWidget
import uk.ac.surrey.xw.api.LabeledPanelWidgetKind
import uk.ac.surrey.xw.api.State
import uk.ac.surrey.xw.api.StringProperty
import uk.ac.surrey.xw.api.WidgetKey
import uk.ac.surrey.xw.api.swing.enrichComponent
import uk.ac.surrey.xw.api.swing.newAction
import uk.ac.surrey.xw.api.toRunnable

trait HasTextField {

  def validateText: Either[String, String] = Right(textField.getText)
  def afterTextUpdate(): Unit = {}
  def defaultText = ""

  var text = defaultText

  val textField = new JTextField(text) {
    getInputMap.put(getKeyStroke(VK_ENTER, 0),
      newAction { _ ⇒ transferFocus() })
    getInputMap.put(getKeyStroke(VK_ESCAPE, 0),
      newAction { _ ⇒ setText(text); transferFocus() })
    this.onFocusLost { _ ⇒
      if (text != getText)
        validateText match {
          case Right(t) ⇒
            text = t
            afterTextUpdate()
          case Left(msg) ⇒
            this.showMessage(msg)
            invokeLater { requestFocus() }
        }
    }
  }
}

class TextInputKind[W <: TextInput] extends LabeledPanelWidgetKind[W] {
  val newWidget = new TextInput(_, _, _)
  val name = "TEXT-INPUT"
  val textProperty = new StringProperty[W]("TEXT",
    Some((w, s) ⇒ { w.text = s; w.textField.setText(s) }),
    _.text)
  val defaultProperty = Some(textProperty)
  override def propertySet = super.propertySet ++ Set(textProperty)
}

class TextInput(
  val key: WidgetKey,
  val state: State,
  val ws: GUIWorkspace)
  extends LabeledPanelWidget
  with HasTextField {

  override val kind = new TextInputKind[this.type]
  add(textField, CENTER)
  override def afterTextUpdate() = updateInState(kind.textProperty)
}

class NumericInputKind[W <: NumericInput] extends LabeledPanelWidgetKind[W] {
  val newWidget = new NumericInput(_, _, _)
  val name = "NUMERIC-INPUT"
  val valueProperty = new DoubleProperty[W](
    "VALUE",
    Some((w, d) ⇒ { w.number = d; w.textField.setText(w.format(w.number)) }),
    _.number
  )
  val defaultProperty = Some(valueProperty)
  override def propertySet = super.propertySet ++ Set(valueProperty)
}

class NumericInput(
  val key: WidgetKey,
  val state: State,
  val ws: GUIWorkspace)
  extends LabeledPanelWidget
  with HasTextField {

  override val kind = new NumericInputKind[this.type]

  add(textField, CENTER)

  def format(d: Double): String = format(Double.box(d))
  def format(d: java.lang.Double) = Dump.logoObject(d)

  var number: Double = 0.0
  override def defaultText = format(number)

  override def validateText =
    NumberParser.parse(textField.getText()) match {
      case Left(msg) ⇒ Left(msg)
      case Right(d) ⇒ Right(format(d))
    }

  override def afterTextUpdate() =
    for (d ← NumberParser.parse(textField.getText()).right) {
      number = d
      updateInState(kind.valueProperty)
    }
}
