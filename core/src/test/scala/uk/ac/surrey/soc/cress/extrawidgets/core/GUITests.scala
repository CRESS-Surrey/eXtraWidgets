package uk.ac.surrey.soc.cress.extrawidgets.core

import scala.Array.canBuildFrom
import scala.Option.option2Iterable
import org.nlogo.app.App
import org.scalatest.FunSpec
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.ShouldMatchers
import Strings.CreateTab
import javax.swing.JMenuItem
import NetLogoInitializer.extraWidgetsManager
import uk.ac.surrey.soc.cress.extrawidgets.state.Strings.propertyMustBeNonEmpty
import uk.ac.surrey.soc.cress.extrawidgets.state.Strings.propertyMustBeUnique

class GUITests extends FunSpec with ShouldMatchers with GivenWhenThen {

  describe("The GUI") {

    val gui = extraWidgetsManager.gui
    val reader = extraWidgetsManager.reader

    it("should add '" + CreateTab + "' to Tools menu") {
      val items = gui.toolsMenu.getMenuComponents.collect {
        case item: JMenuItem if item.getText == CreateTab ⇒
          item
      }
      items should have size 1
    }

    def tabsMenuItemsText = {
      val menuBar = App.app.frame.getJMenuBar
      val menus = 0 until menuBar.getMenuCount map menuBar.getMenu
      for {
        tabsMenu ← menus.find(_.getText == "Tabs").toSeq
        item ← 0 until tabsMenu.getItemCount map tabsMenu.getItem
      } yield item.getText
    }

    def shouldBeThere(tabID: String) {
      reader.contains(tabID) should be(true)
      gui.makeWidgetsMap should contain key tabID
      tabsMenuItemsText should contain(tabID)
    }

    def shouldNotBeThere(tabID: String) {
      reader.contains(tabID) should be(false)
      gui.makeWidgetsMap.contains(tabID) should be(false)
      tabsMenuItemsText should (not contain tabID)
    }

    it("should be able to create extra widgets tabs") {
      gui.addTab("first tab") should be('right)
      shouldBeThere("first tab")
    }

    it("should not able to create tabs with duplicate id") {
      gui.addTab("first tab") shouldEqual Left(propertyMustBeUnique("widget key", "first tab"))
      shouldBeThere("first tab")
    }

    it("should not be able to create tabs with empty id") {
      gui.addTab("") shouldEqual Left(propertyMustBeNonEmpty("widget key"))
      shouldBeThere("first tab")
      shouldNotBeThere("")
    }

    it("should be able to create a second tab with a different id") {
      gui.addTab("second tab") should be('right)
      shouldBeThere("first tab")
      shouldBeThere("second tab")
    }

    it("should be able to remove the first tab while keeping the second one") {
      gui.removeTab("first tab")
      shouldNotBeThere("first tab")
      shouldBeThere("second tab")
    }

    it("should then be able to add a third tab") {
      gui.addTab("third tab")
      shouldNotBeThere("first tab")
      shouldBeThere("second tab")
      shouldBeThere("third tab")
    }

  }

}
