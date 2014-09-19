package uk.ac.surrey.soc.cress.extrawidgets.plugin

import scala.Array.canBuildFrom

import org.nlogo.app.App
import org.nlogo.app.ToolsMenu
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

import javax.swing.JMenuItem
import util.NetLogoInitializer.app

class TabsManagerTests extends FunSuite with ShouldMatchers {

  test("'" + GUIStrings.ToolsMenu.CreateTab + "' added to Tools menu") {
    app.onSuccess {
      case app: App =>
        val jMenuBar = app.frame.getJMenuBar
        val toolsMenu =
          (0 until jMenuBar.getMenuCount)
            .map(jMenuBar.getMenu)
            .collect { case m: ToolsMenu => m }
            .headOption
            .getOrElse(throw new IllegalStateException("Can't find tools menu"))

        val items = toolsMenu.getMenuComponents.collect {
          case item: JMenuItem if item.getText == GUIStrings.ToolsMenu.CreateTab =>
            item
        }
        items should have size 1
    }
  }
}
