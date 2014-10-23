package uk.ac.surrey.soc.cress.extrawidgets.input

import java.awt.BorderLayout
import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout._
import java.awt.event.ItemEvent.SELECTED
import org.nlogo.api.Dump
import org.nlogo.api.LogoList
import org.nlogo.api.LogoList.toIterator
import org.nlogo.api.Nobody
import org.nlogo.window.GUIWorkspace
import org.nlogo.window.InterfaceColors.SLIDER_BACKGROUND
import javax.swing.DefaultListCellRenderer
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JPanel
import uk.ac.surrey.soc.cress.extrawidgets.api.JComponentWidget
import uk.ac.surrey.soc.cress.extrawidgets.api.ListPropertyDef
import uk.ac.surrey.soc.cress.extrawidgets.api.ObjectPropertyDef
import uk.ac.surrey.soc.cress.extrawidgets.api.StateUpdater
import uk.ac.surrey.soc.cress.extrawidgets.api.StringPropertyDef
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKey
import uk.ac.surrey.soc.cress.extrawidgets.api.swing.enrichItemSelectable
import javax.swing.JTextField

class TextInput(
  val key: WidgetKey,
  val stateUpdater: StateUpdater,
  ws: GUIWorkspace)
  extends JPanel
  with JComponentWidget {

  setHeight(50)

  setLayout(new BorderLayout())
  setBackground(SLIDER_BACKGROUND) // no separate constant defined in NetLogo

  val textField = new JTextField()
  val textLabel = new JLabel()
  add(textLabel, NORTH)
  add(textField, CENTER)

  val xwLabel = new StringPropertyDef(this,
    textLabel.setText,
    textLabel.getText
  )

  val xwText = new StringPropertyDef(this,
    textField.setText,
    textField.getText
  )

}
