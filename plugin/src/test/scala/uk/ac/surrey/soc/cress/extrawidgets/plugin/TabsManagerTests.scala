package uk.ac.surrey.soc.cress.extrawidgets.plugin

import scala.Array.canBuildFrom

import org.scalatest.FunSpec
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.ShouldMatchers

import GUIStrings.Data.propertyMustBeNonEmpty
import GUIStrings.Data.propertyMustBeUnique
import akka.dispatch.Await
import akka.util.duration.intToDurationInt
import javax.swing.JMenuItem
import util.NetLogoInitializer.extraWidgetsPlugin

class TabsManagerTests extends FunSpec with ShouldMatchers with GivenWhenThen {

  describe("The TabsManager") {

    val ewp = Await.result(extraWidgetsPlugin, 30 seconds)
    val tm = ewp.tabsManager

    it("should add '" + GUIStrings.ToolsMenu.CreateTab + "' to Tools menu") {
      for (ewp ← extraWidgetsPlugin) {
        val items = ewp.tabsManager.toolsMenu.getMenuComponents.collect {
          case item: JMenuItem if item.getText == GUIStrings.ToolsMenu.CreateTab ⇒
            item
        }
        items should have size 1
      }
    }

    it("should be able to create extra widgets tabs") {
      tm.addTab("first tab") should be('right)
    }

    it("should not able to create tabs with duplicate id") {
      tm.addTab("first tab") shouldEqual Left(propertyMustBeUnique("id", "first tab"))
    }

    it("should not be able to create tabs with empty id") {
      tm.addTab("") shouldEqual Left(propertyMustBeNonEmpty("id"))
    }

    it("should be able to create a second tab with a different id") {
      tm.addTab("second tab") should be('right)
    }
  }

}
