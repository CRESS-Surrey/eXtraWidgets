package uk.ac.surrey.soc.cress.extrawidgets.core

import javax.swing.JPanel
import uk.ac.surrey.soc.cress.extrawidgets.api.ExtraWidget
import uk.ac.surrey.soc.cress.extrawidgets.api.WidgetKey

class ExtraWidgetsTab(
  val key: WidgetKey,
  val label: String)
  extends JPanel with ExtraWidget {

}
