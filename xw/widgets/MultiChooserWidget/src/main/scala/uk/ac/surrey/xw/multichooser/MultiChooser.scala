package uk.ac.surrey.xw.multichooser

import java.awt.BorderLayout.CENTER

import javax.swing.BorderFactory
import javax.swing.JList
import javax.swing.JScrollPane
import javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION

import org.nlogo.core.LogoList
import org.nlogo.window.GUIWorkspace

import uk.ac.surrey.xw.api.IntegerProperty
import uk.ac.surrey.xw.api.LabeledPanelWidget
import uk.ac.surrey.xw.api.LabeledPanelWidgetKind
import uk.ac.surrey.xw.api.ListProperty
import uk.ac.surrey.xw.api.State
import uk.ac.surrey.xw.api.WidgetKey
import uk.ac.surrey.xw.api.swing.LogoObjectListCellRenderer
import uk.ac.surrey.xw.api.swing.enrichJList

class MultiChooserKind[W <: MultiChooser] extends LabeledPanelWidgetKind[W] {

  override val name = "MULTI-CHOOSER"
  override val newWidget = new MultiChooser(_, _, _)

  override val heightProperty = new IntegerProperty[W](
    "HEIGHT", Some(_.setHeight(_)), _.getHeight, 100)

  private def items(jl: JList[AnyRef]) =
    (0 until jl.getModel.getSize).map(jl.getModel.getElementAt)

  val selectedItemsProperty = new ListProperty[W](
    "SELECTED-ITEMS",
    Some((w, xs) ⇒ {
      val _items = items(w.jList)
      w.jList.setSelectedIndices(
        xs.map(x ⇒ _items.indexOf(x)).filterNot(_ == -1).toArray
      )
    }),
    w ⇒ LogoList.fromJava(w.jList.getSelectedValuesList)
  )

  val itemsProperty = new ListProperty[W](
    "ITEMS",
    Some((w, xs) ⇒ {
      w.jList.setListData(xs.toVector.toArray)
    }),
    w ⇒ LogoList(items(w.jList): _*)
  )

  override def propertySet = super.propertySet ++ Set(
    selectedItemsProperty, itemsProperty
  )

  override def defaultProperty = Some(selectedItemsProperty)
}

class MultiChooser(
  val key: WidgetKey,
  val state: State,
  val ws: GUIWorkspace)
  extends LabeledPanelWidget {

  override val kind = new MultiChooserKind[this.type]

  val jList = new JList[AnyRef]() {
    setSelectionMode(MULTIPLE_INTERVAL_SELECTION)
    setBorder(BorderFactory.createRaisedBevelBorder)
  }
  add(new JScrollPane(jList), CENTER)

  /* Use a custom renderer so Dump.logoObject is used instead of toString */
  jList.setCellRenderer(new LogoObjectListCellRenderer)

  jList.onValueChanged { event ⇒
    if (!event.getValueIsAdjusting)
      updateInState(kind.selectedItemsProperty)
  }
}
