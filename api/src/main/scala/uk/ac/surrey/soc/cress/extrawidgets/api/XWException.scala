package uk.ac.surrey.soc.cress.extrawidgets.api

case class XWException(message: String, cause: Throwable = null)
  extends Exception(message, cause) {
  if (cause != null) setStackTrace(cause.getStackTrace)
}
