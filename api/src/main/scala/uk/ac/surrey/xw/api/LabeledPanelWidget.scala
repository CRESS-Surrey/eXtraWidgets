package uk.ac.surrey.xw.api

import java.awt.BorderLayout
import java.awt.BorderLayout.NORTH

import javax.swing.JLabel
import javax.swing.JPanel

abstract class LabeledPanelWidgetKind[W <: LabeledPanelWidget]
  extends JComponentWidgetKind[W] {
  override val heightProperty = new IntegerProperty[W](
    "HEIGHT", Some(_.setHeight(_)), _.getHeight, 50)
  override def propertySet = super.propertySet ++ Set(
    new StringProperty[W]("LABEL", Some(_.setText(_)), _.getText)
  )
}

trait LabeledPanelWidget
  extends JPanel
  with JComponentWidget {
  setLayout(new BorderLayout())
  def setText(text: String) = label.setText(Option(text).getOrElse(key))
  def getText = label.getText
  val label = new JLabel(key)
  def labelPosition = NORTH
  add(label, labelPosition)
}
