package uk.ac.surrey.soc.cress.extrawidgets.api

import org.nlogo.api.ExtensionException

case class XWException(message: String, cause: Exception = null)
  extends ExtensionException(message, cause) {
  if (cause != null) setStackTrace(cause.getStackTrace)
}
