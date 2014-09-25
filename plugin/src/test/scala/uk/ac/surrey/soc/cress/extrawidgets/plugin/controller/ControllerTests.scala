package uk.ac.surrey.soc.cress.extrawidgets.plugin.controller

import org.scalatest.FunSpec
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.ShouldMatchers

import uk.ac.surrey.soc.cress.extrawidgets.plugin.NetLogoInitializer.extraWidgetsPlugin
import uk.ac.surrey.soc.cress.extrawidgets.plugin.model.Strings.propertyMustBeNonEmpty
import uk.ac.surrey.soc.cress.extrawidgets.plugin.model.Strings.propertyMustBeUnique

class ControllerTests extends FunSpec with ShouldMatchers with GivenWhenThen {

  describe("The Controller") {

    val controller = extraWidgetsPlugin.controller
    val reader = extraWidgetsPlugin.reader
    val gui = extraWidgetsPlugin.gui

    it("should be able to create extra widgets tabs") {
      controller.addTab("first tab") should be('right)
      reader.widgetMap.get("first tab") should not be 'empty
      gui.makeWidgetsMap.get("first tab") should not be 'empty
    }

    it("should not able to create tabs with duplicate id") {
      controller.addTab("first tab") shouldEqual Left(propertyMustBeUnique("id", "first tab"))
      reader.widgetMap.get("first tab") should not be 'empty
      gui.makeWidgetsMap.get("first tab") should not be 'empty
    }

    it("should not be able to create tabs with empty id") {
      controller.addTab("") shouldEqual Left(propertyMustBeNonEmpty("id"))
      reader.widgetMap.get("first tab") should not be 'empty
      gui.makeWidgetsMap.get("first tab") should not be 'empty
      reader.widgetMap.get("") should be('empty)
      gui.makeWidgetsMap.get("") should be('empty)
    }

    it("should be able to create a second tab with a different id") {
      controller.addTab("second tab") should be('right)
      reader.widgetMap.get("first tab") should not be 'empty
      gui.makeWidgetsMap.get("first tab") should not be 'empty
      reader.widgetMap.get("second tab") should not be 'empty
      gui.makeWidgetsMap.get("second tab") should not be 'empty
    }
  }
}