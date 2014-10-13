package uk.ac.surrey.soc.cress.extrawidgets.slider

import org.nlogo.window.GUIWorkspace

import javax.swing.JSlider
import uk.ac.surrey.soc.cress.extrawidgets.api.JComponentWidget
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyMap
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKey

class Slider(val key: WidgetKey, properties: PropertyMap, ws: GUIWorkspace)
  extends JSlider with JComponentWidget {

}
