package uk.ac.surrey.xw.extension.prim

import java.io.BufferedWriter
import java.io.FileWriter
import java.io.PrintWriter

import org.nlogo.api.Argument
import org.nlogo.api.Command
import org.nlogo.api.Context
import org.nlogo.core.Syntax.StringType
import org.nlogo.core.Syntax.commandSyntax
import org.nlogo.nvm.ExtensionContext

import uk.ac.surrey.xw.api.XWException
import uk.ac.surrey.xw.state.Reader

class Export(reader: Reader) extends Command {
  override def getSyntax = commandSyntax(List(StringType))
  def perform(args: Array[Argument], context: Context): Unit = {
    val ws = context.asInstanceOf[ExtensionContext].workspace
    val filePath = args(0).getString
    val printWriter = try {
      val fileName = ws.fileManager.attachPrefix(filePath)
      new PrintWriter(new BufferedWriter(new FileWriter(fileName)))
    } catch {
      case ex: java.io.IOException ⇒ throw XWException(ex.getMessage, ex)
      case ex: java.net.MalformedURLException ⇒ throw XWException(ex.getMessage, ex)
      case ex: java.lang.IllegalStateException ⇒ throw XWException(ex.getMessage)
    }
    try {
      printWriter.write(reader.toJSON)
    } catch {
      case ex: java.io.IOException ⇒ throw XWException(ex.getMessage, ex)
    } finally {
      try printWriter.close()
      catch { case _: java.io.IOException ⇒ } // give up
    }
  }
}
