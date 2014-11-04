package uk.ac.surrey.soc.cress.extrawidgets.api

import java.awt.BorderLayout
import java.awt.BorderLayout.NORTH

import org.nlogo.window.InterfaceColors.SLIDER_BACKGROUND

import javax.swing.JLabel
import javax.swing.JPanel

trait LabeledPanelWidget
  extends JPanel
  with JComponentWidget {

  setHeight(50)
  setLayout(new BorderLayout())
  setBackground(SLIDER_BACKGROUND)

  val label = new JLabel(key)
  def labelPosition = NORTH
  add(label, labelPosition)

  val xwLabel = new StringProperty(label.setText, label.getText)
}
