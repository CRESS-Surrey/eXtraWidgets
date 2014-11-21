package uk.ac.surrey.xw.api.swing

import org.nlogo.api.Dump

import javax.swing.DefaultListCellRenderer
import javax.swing.JList

class LogoObjectListCellRenderer extends DefaultListCellRenderer {
  override def getListCellRendererComponent(
    list: JList, value: AnyRef, index: Int,
    isSelected: Boolean, cellHasFocus: Boolean) = {
    super.getListCellRendererComponent(list, value, index,
      isSelected, cellHasFocus)
    setText(Option(value).map(Dump.logoObject).getOrElse(""))
    this
  }
}
