package uk.ac.surrey.soc.cress.extrawidgets.plugin

import org.nlogo.app.App
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FunSuite

class TabsManagerTests extends FunSuite with BeforeAndAfterAll {

  var ewp: ExtraWidgetsPlugin = null

  override def beforeAll() {
    // start NetLogo
    App.main(Array[String]())
    ewp = new ExtraWidgetsPlugin(App.app)
  }

}
