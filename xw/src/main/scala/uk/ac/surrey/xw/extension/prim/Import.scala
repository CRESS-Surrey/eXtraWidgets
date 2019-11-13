package uk.ac.surrey.xw.extension.prim

import scala.io.Source

import org.nlogo.api.Argument
import org.nlogo.api.Command
import org.nlogo.api.Context
import org.nlogo.core.Syntax.StringType
import org.nlogo.core.Syntax.commandSyntax
import org.nlogo.nvm.ExtensionContext

import uk.ac.surrey.xw.api.XWException
import uk.ac.surrey.xw.state.JSONLoader
import uk.ac.surrey.xw.state.Writer

class Import(writer: Writer) extends Command {
  override def getSyntax = commandSyntax(List(StringType))
  def perform(args: Array[Argument], context: Context): Unit = {
    val ws = context.asInstanceOf[ExtensionContext].workspace
    val filePath = args(0).getString
    val source = try {
      val fileName = ws.fileManager.attachPrefix(filePath)
      Source.fromFile(fileName)
    } catch {
      case ex: java.io.IOException ⇒ throw XWException(ex.getMessage, ex)
      case ex: java.net.MalformedURLException ⇒ throw XWException(ex.getMessage, ex)
      case ex: java.lang.IllegalStateException ⇒ throw XWException(ex.getMessage)
    }
    try {
      new JSONLoader(writer).load(source.getLines.mkString("\n"))
    } catch {
      case ex: java.io.IOException ⇒ throw XWException(ex.getMessage, ex)
    } finally {
      try source.close()
      catch { case _: java.io.IOException ⇒ } // give up
    }
  }
}
