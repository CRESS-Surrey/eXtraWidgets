package uk.ac.surrey.xw.extension.prim

import org.nlogo.api.Argument
import org.nlogo.api.Context
import org.nlogo.api.Reporter
import org.nlogo.core.LogoList
import org.nlogo.core.Syntax.ListType
import org.nlogo.core.Syntax.reporterSyntax

import uk.ac.surrey.xw.api.KindName
import uk.ac.surrey.xw.state.Reader

class KindList(kindName: KindName, reader: Reader) extends Reporter {
  override def getSyntax = reporterSyntax(ret = ListType)
  def report(args: Array[Argument], context: Context): AnyRef =
    LogoList.fromVector(
      reader.widgetKeyVector.filter {
        reader.get(reader.kindPropertyKey, _) == kindName
      }
    )
}
