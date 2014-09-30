package uk.ac.surrey.soc.cress.extrawidgets.plugin.util

case class XWException(message: String, cause: Throwable = null)
  extends Exception(message, cause) {
  if (cause != null) setStackTrace(cause.getStackTrace)
}
