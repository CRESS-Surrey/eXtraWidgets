package uk.ac.surrey.soc.cress.extrawidgets.plugin.model

import uk.ac.surrey.soc.cress.extrawidgets.plugin.util._

class Writer(store: MutableStore) {
  val reader = new Reader(store)
  def add(kind: Kind, id: String): Either[String, Unit] =
    for {
      _ ← reader.validateNonEmpty("id", id)
      _ ← reader.validateUnique("id", id)
    } yield {
      val w = newWidgetMap
      w += "kind" -> kind
      store += id -> w
      println(store)
    }
}