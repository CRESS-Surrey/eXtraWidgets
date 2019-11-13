package uk.ac.surrey.xw.api.swing;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.nlogo.api.Dump;

@SuppressWarnings("serial")
public class LogoObjectListCellRenderer extends DefaultListCellRenderer {

  @Override
  public Component getListCellRendererComponent(JList<? extends Object> list, Object value,
      int index, boolean isSelected, boolean cellHasFocus) {
    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    setText(value == null ? "" : Dump.logoObject(value));
    return this;
  }

}
