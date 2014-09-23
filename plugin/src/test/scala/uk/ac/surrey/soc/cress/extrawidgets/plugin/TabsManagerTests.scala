package uk.ac.surrey.soc.cress.extrawidgets.plugin

import scala.Array.canBuildFrom

import org.scalatest.FunSpec
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.ShouldMatchers

import controller.Strings.CreateTab
import javax.swing.JMenuItem
import util.NetLogoInitializer.extraWidgetsPlugin

class TabsManagerTests extends FunSpec with ShouldMatchers with GivenWhenThen {

  describe("The TabsManager") {

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
