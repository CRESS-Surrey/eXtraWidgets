package uk.ac.surrey.xw.chooser

import java.awt.BorderLayout.CENTER
import java.awt.event.ItemEvent.SELECTED

import javax.swing.JComboBox

import org.nlogo.core.LogoList
import org.nlogo.core.Nobody
import org.nlogo.window.GUIWorkspace

import uk.ac.surrey.xw.api.LabeledPanelWidget
import uk.ac.surrey.xw.api.LabeledPanelWidgetKind
import uk.ac.surrey.xw.api.ListProperty
import uk.ac.surrey.xw.api.ObjectProperty
import uk.ac.surrey.xw.api.State
import uk.ac.surrey.xw.api.WidgetKey
import uk.ac.surrey.xw.api.swing.LogoObjectListCellRenderer
import uk.ac.surrey.xw.api.swing.enrichItemSelectable

class ChooserKind[W <: Chooser] extends LabeledPanelWidgetKind[W] {

  override val name = "CHOOSER"
  override val newWidget = new Chooser(_, _, _)

  val selectedItemProperty = new ObjectProperty[W](
    "SELECTED-ITEM",
    Some(_.combo.setSelectedItem(_)),
    w ⇒ Option(w.combo.getSelectedItem).getOrElse(Nobody)
  )

  val itemsProperty = new ListProperty[W](
    "ITEMS",
    Some((w, xs) ⇒ {
      w.combo.removeAllItems()
      xs.foreach(w.combo.addItem(_))
      w.combo.setSelectedItem(xs.toVector.headOption.orNull)
    }),
    w ⇒ LogoList((0 until w.combo.getItemCount).map(w.combo.getItemAt): _*)
  )

  override def propertySet = super.propertySet ++ Set(
    selectedItemProperty, itemsProperty
  )

  override def defaultProperty = Some(selectedItemProperty)
}

class Chooser(
  val key: WidgetKey,
  val state: State,
  val ws: GUIWorkspace)
  extends LabeledPanelWidget {

  override val kind = new ChooserKind[this.type]

  val combo = new JComboBox[AnyRef]()
  add(combo, CENTER)

  /* Use a custom renderer so Dump.logoObject is used instead of toString */
  combo.setRenderer(new LogoObjectListCellRenderer)

  combo.onItemStateChanged { event ⇒
    if (event.getStateChange == SELECTED)
      updateInState(kind.selectedItemProperty)
  }
}
