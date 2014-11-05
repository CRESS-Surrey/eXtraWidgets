package uk.ac.surrey.soc.cress.extrawidgets.extension

import uk.ac.surrey.soc.cress.extrawidgets.api._
import uk.ac.surrey.soc.cress.extrawidgets.api.XWException

trait WidgetContextProvider {
  def currentContext: WidgetKey
}

class WidgetContextManager extends WidgetContextProvider {
  var stack: List[WidgetKey] = List.empty
  def clear(): Unit = stack = List.empty
  def currentContext: WidgetKey =
    stack.headOption.getOrElse(throw XWException("""
      |Current widget context undefined! Most likely, the primitive
      | you are trying to use should be called from inside a
      | block passed to xw:create-<kind>, xw:ask or xw:of.
      |""".stripMargin))
  def withContext[A](key: WidgetKey)(f: () ⇒ A): A = {
    stack = normalizeString(key) :: stack // push
    val result = f()
    stack = stack.tail // pop
    result
  }
}
