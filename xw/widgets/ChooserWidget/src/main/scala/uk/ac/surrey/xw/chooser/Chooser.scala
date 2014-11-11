package uk.ac.surrey.xw.chooser

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
import uk.ac.surrey.xw.api.LabeledPanelWidget
import uk.ac.surrey.xw.api.ListProperty
import uk.ac.surrey.xw.api.ObjectProperty
import uk.ac.surrey.xw.api.StateUpdater
import uk.ac.surrey.xw.api.WidgetKey
import uk.ac.surrey.xw.api.annotations.DefaultProperty
import uk.ac.surrey.xw.api.swing.enrichItemSelectable

@DefaultProperty("SELECTED-ITEM")
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

  val xwItems = new ListProperty(
    xs ⇒ {
      combo.removeAllItems()
      xs.foreach(combo.addItem(_))
      combo.setSelectedItem(xs.toVector.headOption.orNull)
    },
    () ⇒ LogoList((0 until combo.getItemCount).map(combo.getItemAt): _*)
  )

  val xwSelectedItem = new ObjectProperty(
    combo.setSelectedItem,
    () ⇒ Option(combo.getSelectedItem).getOrElse(Nobody)
  )

  combo.onItemStateChanged { event ⇒
    if (event.getStateChange == SELECTED)
      updateInState(xwSelectedItem)
  }
}
