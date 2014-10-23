package uk.ac.surrey.soc.cress.extrawidgets.chooser

import java.awt.BorderLayout.CENTER
import java.awt.event.ItemEvent.SELECTED

import org.nlogo.api.Dump
import org.nlogo.api.LogoList
import org.nlogo.api.LogoList.toIterator
import org.nlogo.api.Nobody
import org.nlogo.window.GUIWorkspace

import javax.swing.DefaultListCellRenderer
import javax.swing.JComboBox
import javax.swing.JList
import uk.ac.surrey.soc.cress.extrawidgets.api.LabeledPanelWidget
import uk.ac.surrey.soc.cress.extrawidgets.api.ListPropertyDef
import uk.ac.surrey.soc.cress.extrawidgets.api.ObjectPropertyDef
import uk.ac.surrey.soc.cress.extrawidgets.api.StateUpdater
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKey
import uk.ac.surrey.soc.cress.extrawidgets.api.swing.enrichItemSelectable

class Chooser(
  val key: WidgetKey,
  val stateUpdater: StateUpdater,
  ws: GUIWorkspace)
  extends LabeledPanelWidget {

  val combo = new JComboBox()
  add(combo, CENTER)

  /* Use a custom renderer so Dump.logoObject is used instead of toString */
  combo.setRenderer(new DefaultListCellRenderer {
    override def getListCellRendererComponent(
      list: JList, value: AnyRef, index: Int,
      isSelected: Boolean, cellHasFocus: Boolean) = {
      super.getListCellRendererComponent(list, value, index,
        isSelected, cellHasFocus)
      setText(Option(value).map(Dump.logoObject).getOrElse(""))
      this
    }
  })

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
