package uk.ac.surrey.soc.cress.extrawidgets.plugin

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

import scala.Array.canBuildFrom

import org.nlogo.app.App
import org.nlogo.app.ToolsMenu
import org.scalatest.FunSuite

import akka.dispatch.Promise
import javax.swing.JMenuItem
import uk.ac.surrey.soc.cress.extrawidgets.plugin.util.SwingExecutionContext.swingExecutionContext

class TabsManagerTests extends FunSuite with PropertyChangeListener {

  App.main(Array[String]())
  val ewp = new ExtraWidgetsPlugin(App.app)
  val toolsMenu = Promise[ToolsMenu]()

  ewp.addPropertyChangeListener(this)

  def propertyChange(e: PropertyChangeEvent): Unit = {
    if (e.getPropertyName == "toolsMenu")
      toolsMenu.success(ewp.toolsMenu)
  }

  test("'" + GUIStrings.ToolsMenu.CreateTab + "' added to Tools menu") {
    toolsMenu.onSuccess {
      case tm: ToolsMenu =>
        val items = tm.getMenuComponents.collect {
          case item: JMenuItem if item.getText == GUIStrings.ToolsMenu.CreateTab =>
            item
        }
        assert(items.size === 1)
    }
  }

}
