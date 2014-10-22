package uk.ac.surrey.soc.cress.extrawidgets.chooser

import java.awt.BorderLayout
import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout.PAGE_END
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

class Chooser(
  val key: WidgetKey,
  val stateUpdater: StateUpdater,
  ws: GUIWorkspace)
  extends JPanel
  with JComponentWidget {

  setHeight(50)

  setLayout(new BorderLayout())
  setBackground(SLIDER_BACKGROUND) // no separate constant defined in NetLogo

  val combo = new JComboBox()
  val textLabel = new JLabel()
  add(textLabel, CENTER)
  add(combo, PAGE_END)

  /* Use a custom renderer so Dump.logoObject is used instead of toString */
  combo.setRenderer(new DefaultListCellRenderer {
    override def getListCellRendererComponent(
      list: JList, value: AnyRef, index: Int,
      isSelected: Boolean, cellHasFocus: Boolean) = {
      super.getListCellRendererComponent(list, value, index,
        isSelected, cellHasFocus)
      setText(Dump.logoObject(value))
      this
    }
  })

  val xwText = new StringPropertyDef(this,
    textLabel.setText,
    textLabel.getText
  )

  val xwItems = new ListPropertyDef(this,
    xs ⇒ {
      combo.removeAllItems()
      xs.foreach(combo.addItem(_))
      combo.setSelectedItem(xs.toVector.headOption.orNull)
    },
    () ⇒ LogoList((0 until combo.getItemCount).map(combo.getItemAt): _*)
  )

  val xwSelectedItem = new ObjectPropertyDef(this,
    combo.setSelectedItem,
    () ⇒ Option(combo.getSelectedItem).getOrElse(Nobody)
  )

  combo.onItemStateChanged { event ⇒
    if (event.getStateChange == SELECTED)
      xwSelectedItem.updateInState()
  }
}
