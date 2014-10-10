package uk.ac.surrey.soc.cress.extrawidgets.note

import org.nlogo.window.GUIWorkspace

import javax.swing.JLabel
import uk.ac.surrey.soc.cress.extrawidgets.api.JComponentWidget
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyMap
import uk.ac.surrey.soc.cress.extrawidgets.api.StringPropertyDef
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKey

class Note(val key: WidgetKey, properties: PropertyMap, ws: GUIWorkspace)
  extends JLabel with JComponentWidget {

  val text = new StringPropertyDef(this, setText, getText, java.lang.Math.random().toString)

}
