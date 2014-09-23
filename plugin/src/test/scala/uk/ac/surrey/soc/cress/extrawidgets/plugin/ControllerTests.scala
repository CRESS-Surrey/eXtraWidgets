package uk.ac.surrey.soc.cress.extrawidgets.plugin

import org.scalatest.FunSpec
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.ShouldMatchers

import model.Strings.propertyMustBeNonEmpty
import model.Strings.propertyMustBeUnique
import util.NetLogoInitializer.extraWidgetsPlugin

class ControllerTests extends FunSpec with ShouldMatchers with GivenWhenThen {

  describe("The Controller") {

    val controller = extraWidgetsPlugin.controller

    it("should be able to create extra widgets tabs") {
      controller.addTab("first tab") should be('right)
    }

    it("should not able to create tabs with duplicate id") {
      controller.addTab("first tab") shouldEqual Left(propertyMustBeUnique("id", "first tab"))
    }

    it("should not be able to create tabs with empty id") {
      controller.addTab("") shouldEqual Left(propertyMustBeNonEmpty("id"))
    }

    it("should be able to create a second tab with a different id") {
      controller.addTab("second tab") should be('right)
    }
  }
}