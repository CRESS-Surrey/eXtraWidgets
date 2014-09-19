package uk.ac.surrey.soc.cress.extrawidgets.plugin

import scala.Array.canBuildFrom

import org.nlogo.app.App
import org.nlogo.app.ToolsMenu
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

import javax.swing.JMenuItem
import util.NetLogoInitializer.extraWidgetsPlugin

class TabsManagerTests extends FunSuite with ShouldMatchers {

  test("'" + GUIStrings.ToolsMenu.CreateTab + "' added to Tools menu") {
    for (ewp ← extraWidgetsPlugin) {
      val items = ewp.toolsMenu.getMenuComponents.collect {
        case item: JMenuItem if item.getText == GUIStrings.ToolsMenu.CreateTab ⇒
          item
      }
      items should have size 1
    }
  }

}
