package uk.ac.surrey.xw.button

import javax.swing.JButton

import org.nlogo.api.JobOwner
import org.nlogo.api.SimpleJobOwner
import org.nlogo.core.AgentKind.Observer
import org.nlogo.core.CompilerException
import org.nlogo.nvm.Workspace
import org.nlogo.theme.InterfaceColors
import org.nlogo.window.GUIWorkspace

import uk.ac.surrey.xw.api.ColorProperty
import uk.ac.surrey.xw.api.ComponentWidget
import uk.ac.surrey.xw.api.ComponentWidgetKind
import uk.ac.surrey.xw.api.IntegerProperty
import uk.ac.surrey.xw.api.State
import uk.ac.surrey.xw.api.StringProperty
import uk.ac.surrey.xw.api.WidgetKey
import uk.ac.surrey.xw.api.swing.enrichAbstractButton

class ButtonKind[W <: Button] extends ComponentWidgetKind[W] {
  override val name = "BUTTON"
  override val newWidget = new Button(_, _, _)
  override val colorProperty = new ColorProperty[W](
    "COLOR", Some(_.setBackground(_)), _.getBackground, InterfaceColors.buttonBackground())
  override val heightProperty = new IntegerProperty[W](
    "HEIGHT", Some(_.setHeight(_)), _.getHeight, 50)
  val labelProperty = new StringProperty[W](
    "LABEL", Some(_.setText(_)), _.getText)

  val commandsProperty = new StringProperty[W](
    "COMMANDS", Some(_.commands = _), _.commands)

  val defaultProperty = None
  override def propertySet = super.propertySet ++
    Set(labelProperty, commandsProperty)
}

object ButtonCommandRunner {
  def run(ws: Workspace, owner: JobOwner, commands: String, warn: String => Unit): Unit =
    try ws.evaluateCommands(owner, commands, ws.world.observers, false)
    catch { case e: CompilerException => warn(e.getMessage) }
}

class Button(
  val key: WidgetKey,
  val state: State,
  val ws: GUIWorkspace)
  extends JButton
  with ComponentWidget {
  setBorderPainted(false)
  val kind: ButtonKind[this.type] = new ButtonKind[this.type]
  var commands = ""
  val owner = new SimpleJobOwner(key, ws.world.mainRNG, Observer) {
    override def isButton = true
    override def ownsPrimaryJobs = true
  }
  this.onActionPerformed { _ =>
    ButtonCommandRunner.run(ws, owner, commands, ws.warningMessage)
  }
}
