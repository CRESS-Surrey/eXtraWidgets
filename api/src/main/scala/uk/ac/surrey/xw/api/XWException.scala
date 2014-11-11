package uk.ac.surrey.xw.api

import org.nlogo.api.ExtensionException

case class XWException(message: String, cause: Exception = null)
  extends ExtensionException(message, cause) {
  if (cause != null) setStackTrace(cause.getStackTrace)
}
