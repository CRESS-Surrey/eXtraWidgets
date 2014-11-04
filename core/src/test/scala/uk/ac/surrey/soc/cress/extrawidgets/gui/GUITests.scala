package uk.ac.surrey.soc.cress.extrawidgets.gui

import scala.Array.canBuildFrom
import scala.Option.option2Iterable

import org.nlogo.app.App
import org.nlogo.awt.EventQueue.invokeAndWait
import org.scalatest.FunSpec
import org.scalatest.GivenWhenThen
import org.scalatest.matchers.ShouldMatchers

import NetLogoInitializer.manager
import Strings.CreateTab
import javax.swing.JMenuItem
import uk.ac.surrey.soc.cress.extrawidgets.api.PropertyKey
import uk.ac.surrey.soc.cress.extrawidgets.api.XWException
import uk.ac.surrey.soc.cress.extrawidgets.api.toRunnable
import uk.ac.surrey.soc.cress.extrawidgets.state.Strings.propertyMustBeNonEmpty
import uk.ac.surrey.soc.cress.extrawidgets.state.Strings.propertyMustBeUnique

class GUITests extends FunSpec with ShouldMatchers with GivenWhenThen {

  describe("The GUI") {

    val reader = manager.reader
    val writer = manager.writer

    def addTab(key: PropertyKey) = writer.add(key, Map("kind" -> "tab"))

    it("should add '" + CreateTab + "' to Tools menu") {
      val items = manager.toolsMenu.getMenuComponents.collect {
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
      invokeAndWait {
        reader.contains(tabID) should be(true)
        manager.gui.getWidget(tabID) should be('defined)
        tabsMenuItemsText should contain(tabID)
      }
    }

    def shouldNotBeThere(tabID: String) {
      invokeAndWait {
        reader.contains(tabID) should be(false)
        manager.gui.getWidget(tabID) should be('empty)
        tabsMenuItemsText should (not contain tabID)
      }
    }

    it("should be able to create extra widgets tabs") {
      invokeAndWait { addTab("first tab") }
      shouldBeThere("FIRST TAB")
    }

    it("should not able to create tabs with duplicate id") {
      val res = addTab("first tab")
      invokeAndWait {
        res shouldEqual
          Left(XWException(propertyMustBeUnique("widget key", "FIRST TAB")))
      }
      shouldBeThere("FIRST TAB")
    }

    it("should not be able to create tabs with empty id") {
      val res = addTab("")
      invokeAndWait {
        res shouldEqual
          Left(XWException(propertyMustBeNonEmpty("widget key")))
      }
      shouldBeThere("FIRST TAB")
      shouldNotBeThere("")
    }

    it("should be able to create a second tab with a different id") {
      invokeAndWait { addTab("second tab") }
      shouldBeThere("FIRST TAB")
      shouldBeThere("SECOND TAB")
    }

    it("should be able to remove the first tab while keeping the second one") {
      writer.remove("first tab")
      shouldBeThere("SECOND TAB")
      shouldNotBeThere("FIRST TAB")
    }

    it("should then be able to add a third tab") {
      addTab("third tab")
      shouldNotBeThere("FIRST TAB")
      shouldBeThere("SECOND TAB")
      shouldBeThere("THIRD TAB")
    }

  }

}
