package uk.ac.surrey.xw.extension.prim

import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.DefaultReporter
import org.nlogo.api.LogoList
import org.nlogo.api.Syntax.ListType
import org.nlogo.api.Syntax.reporterSyntax

import uk.ac.surrey.xw.state.Reader

class Widgets(reader: Reader) extends DefaultReporter {
  override def getSyntax = reporterSyntax(ListType)
  def report(args: Array[Argument], context: Context): AnyRef =
    LogoList.fromVector(
      reader.widgetKeyVector.filter {
        reader.get(reader.kindPropertyKey, _) != reader.tabPropertyKey
      }
    )
}
