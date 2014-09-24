package uk.ac.surrey.soc.cress.extrawidgets.plugin.gui

import scala.Array.canBuildFrom

import org.scalatest.FunSpec
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.ShouldMatchers

import Strings.CreateTab
import javax.swing.JMenuItem
import uk.ac.surrey.soc.cress.extrawidgets.plugin.NetLogoInitializer.extraWidgetsPlugin

class GUITests extends FunSpec with ShouldMatchers with GivenWhenThen {

  describe("The GUI") {

    val tm = extraWidgetsPlugin.tabsManager

    it("should add '" + CreateTab + "' to Tools menu") {
      val items = tm.toolsMenu.getMenuComponents.collect {
        case item: JMenuItem if item.getText == CreateTab ⇒
          item
      }
      items should have size 1
    }

  }

}
