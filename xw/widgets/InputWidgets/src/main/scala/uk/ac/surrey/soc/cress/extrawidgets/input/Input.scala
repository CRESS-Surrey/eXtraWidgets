package uk.ac.surrey.soc.cress.extrawidgets.input

import java.awt.BorderLayout.CENTER
import java.awt.event.KeyEvent.VK_ENTER
import java.awt.event.KeyEvent.VK_ESCAPE

import org.nlogo.api.Dump
import org.nlogo.api.NumberParser
import org.nlogo.awt.EventQueue.invokeLater
import org.nlogo.window.GUIWorkspace

import javax.swing.JTextField
import javax.swing.KeyStroke.getKeyStroke
import uk.ac.surrey.soc.cress.extrawidgets.api.DoublePropertyDef
import uk.ac.surrey.soc.cress.extrawidgets.api.LabeledPanelWidget
import uk.ac.surrey.soc.cress.extrawidgets.api.StateUpdater
import uk.ac.surrey.soc.cress.extrawidgets.api.StringPropertyDef
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKey
import uk.ac.surrey.soc.cress.extrawidgets.api.swing.enrichComponent
import uk.ac.surrey.soc.cress.extrawidgets.api.swing.newAction
import uk.ac.surrey.soc.cress.extrawidgets.api.toRunnable

trait HasTextField {

  def validateText: Either[String, String] = Right(textField.getText)
  def afterTextUpdate(): Unit = {}
  def defaultText = ""

  protected var text = defaultText

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

class TextInput(
  val key: WidgetKey,
  val stateUpdater: StateUpdater,
  ws: GUIWorkspace)
  extends LabeledPanelWidget
  with HasTextField {

  add(textField, CENTER)

  val xwText = new StringPropertyDef(this,
    s ⇒ { text = s; textField.setText(s) },
    () ⇒ text
  )

  override def afterTextUpdate() =
    updatePropertyInState(xwText)
}

class NumericInput(
  val key: WidgetKey,
  val stateUpdater: StateUpdater,
  ws: GUIWorkspace)
  extends LabeledPanelWidget
  with HasTextField {

  add(textField, CENTER)

  def format(d: Double): String = format(Double.box(d))
  def format(d: java.lang.Double) = Dump.logoObject(d)

  var number: Double = 0.0
  override def defaultText = format(number)

  val xwNumber = new DoublePropertyDef(this,
    d ⇒ { number = d; textField.setText(format(number)) },
    () ⇒ number
  )

  override def validateText =
    NumberParser.parse(textField.getText()) match {
      case Left(msg) ⇒ Left(msg)
      case Right(d) ⇒ Right(format(d))
    }

  override def afterTextUpdate() =
    for (d ← NumberParser.parse(textField.getText()).right) {
      number = d
      updatePropertyInState(xwNumber)
    }
}
