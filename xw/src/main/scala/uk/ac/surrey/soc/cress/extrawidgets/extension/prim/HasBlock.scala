package uk.ac.surrey.soc.cress.extrawidgets.extension.prim

import org.nlogo.agent.ArrayAgentSet
import org.nlogo.api.Context
import org.nlogo.nvm.AssemblerAssistant
import org.nlogo.nvm.CustomAssembled
import org.nlogo.nvm.ExtensionContext

trait HasBlock extends CustomAssembled {
  def runBlock(context: Context): Unit = {
    val extContext = context.asInstanceOf[ExtensionContext]
    val nvmContext = extContext.nvmContext
    val agent = nvmContext.agent
    val agentSet = new ArrayAgentSet(agent.getAgentClass, Array(agent), agent.world)
    nvmContext.runExclusiveJob(agentSet, nvmContext.ip + 1)
  }
  def assemble(a: AssemblerAssistant) {
    a.block()
    a.done()
  }
}
