package uk.ac.surrey.xw.extension.prim

import org.nlogo.agent.ArrayAgentSet
import org.nlogo.api.Context
import org.nlogo.nvm.AssemblerAssistant
import org.nlogo.nvm.CustomAssembled
import org.nlogo.nvm.ExtensionContext

trait HasCommandBlock extends CustomAssembled {
  def runBlock(context: Context): Unit = {
    val extContext = context.asInstanceOf[ExtensionContext]
    val nvmContext = extContext.nvmContext
    val agent = nvmContext.agent
    val agentSet = new ArrayAgentSet(agent.kind, null, Array(agent))
    nvmContext.runExclusiveJob(agentSet, nvmContext.ip + 1)
  }
  def assemble(a: AssemblerAssistant) {
    a.block()
    a.done()
  }
}
