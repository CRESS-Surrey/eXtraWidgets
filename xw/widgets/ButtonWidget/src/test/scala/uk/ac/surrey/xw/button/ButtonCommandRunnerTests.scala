package uk.ac.surrey.xw.button

import org.nlogo.api.SimpleJobOwner
import org.nlogo.core.AgentKind.Observer
import org.nlogo.headless.HeadlessWorkspace
import org.scalatest.funsuite.AnyFunSuite

class ButtonCommandRunnerTests extends AnyFunSuite {

  test("runs button commands through the workspace job machinery") {
    val workspace = HeadlessWorkspace.newInstance
    try {
      workspace.initForTesting(0, "globals [button-ran]")

      val owner = new SimpleJobOwner("test-button", workspace.world.mainRNG, Observer) {
        override def isButton = true
        override def ownsPrimaryJobs = true
      }

      ButtonCommandRunner.run(workspace, owner, "set button-ran 1", _ => ())

      assert(workspace.report("button-ran") == java.lang.Double.valueOf(1))
    } finally {
      workspace.dispose()
    }
  }

  test("reports compile errors through the warning callback") {
    val workspace = HeadlessWorkspace.newInstance
    try {
      workspace.initForTesting(0)

      val owner = new SimpleJobOwner("test-button", workspace.world.mainRNG, Observer) {
        override def isButton = true
        override def ownsPrimaryJobs = true
      }
      var warnings = Vector.empty[String]

      ButtonCommandRunner.run(workspace, owner, "not-a-command", warnings :+= _)

      assert(warnings.nonEmpty)
    } finally {
      workspace.dispose()
    }
  }
}
