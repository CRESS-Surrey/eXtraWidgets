package uk.ac.surrey.xw.extension

import uk.ac.surrey.xw.api.WidgetKey
import uk.ac.surrey.xw.api.XWException

class WidgetContextManager {
  var stack: List[WidgetKey] = List.empty
  def clear(): Unit = stack = List.empty
  def currentContext: WidgetKey =
    stack.headOption.getOrElse(throw XWException("""
      |Current widget context undefined! Most likely, the primitive
      | you are trying to use should be called from inside a
      | block passed to xw:create-<kind>, xw:ask or xw:of.
      |""".stripMargin))
  def withContext[A](key: WidgetKey)(f: () ⇒ A): A = {
    stack = key :: stack // push
    val result = f()
    stack = stack.tail // pop
    result
  }
}
