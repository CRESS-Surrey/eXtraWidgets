package uk.ac.surrey.soc.cress.extrawidgets.plugin.controller

import scala.Option.option2Iterable

import org.nlogo.app.App
import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers

import uk.ac.surrey.soc.cress.extrawidgets.plugin.NetLogoInitializer.extraWidgetsPlugin
import uk.ac.surrey.soc.cress.extrawidgets.state.Strings.propertyMustBeNonEmpty
import uk.ac.surrey.soc.cress.extrawidgets.state.Strings.propertyMustBeUnique

class ControllerTests extends FunSpec with ShouldMatchers {

  describe("The Controller") {

    val controller = extraWidgetsPlugin.controller
    val reader = extraWidgetsPlugin.reader
    val gui = extraWidgetsPlugin.gui

    def tabsMenuItemsText = {
      val menuBar = App.app.frame.getJMenuBar
      val menus = 0 until menuBar.getMenuCount map menuBar.getMenu
      for {
        tabsMenu ← menus.find(_.getText == "Tabs").toSeq
        item ← 0 until tabsMenu.getItemCount map tabsMenu.getItem
      } yield item.getText
    }

    def shouldBeThere(tabID: String) {
      reader.widgetMap should contain key tabID
      gui.makeWidgetsMap should contain key tabID
      tabsMenuItemsText should contain(tabID)
    }

    def shouldNotBeThere(tabID: String) {
      reader.widgetMap.get(tabID) should be('empty)
      gui.makeWidgetsMap.get(tabID) should be('empty)
      tabsMenuItemsText should (not contain tabID)
    }

    it("should be able to create extra widgets tabs") {
      controller.addTab("first tab") should be('right)
      shouldBeThere("first tab")
    }

    it("should not able to create tabs with duplicate id") {
      controller.addTab("first tab") shouldEqual Left(propertyMustBeUnique("id", "first tab"))
      shouldBeThere("first tab")
    }

    it("should not be able to create tabs with empty id") {
      controller.addTab("") shouldEqual Left(propertyMustBeNonEmpty("id"))
      shouldBeThere("first tab")
      shouldNotBeThere("")
    }

    it("should be able to create a second tab with a different id") {
      controller.addTab("second tab") should be('right)
      shouldBeThere("first tab")
      shouldBeThere("second tab")
    }

    it("should be able to remove the first tab while keeping the second one") {
      controller.removeTab("first tab")
      shouldNotBeThere("first tab")
      shouldBeThere("second tab")
    }

    it("should then be able to add a third tab") {
      controller.addTab("third tab")
      shouldNotBeThere("first tab")
      shouldBeThere("second tab")
      shouldBeThere("third tab")
    }
  }
}
