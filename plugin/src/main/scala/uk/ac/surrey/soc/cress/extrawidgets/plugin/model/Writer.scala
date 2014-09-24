package uk.ac.surrey.soc.cress.extrawidgets.plugin.model

import uk.ac.surrey.soc.cress.extrawidgets.plugin.util._

class Writer(widgetMap: MutableWidgetMap, reader: Reader) {
  def add(kind: WidgetKind, id: WidgetID): Either[String, Unit] =
    for {
      _ ← reader.validateNonEmpty("id", id)
      _ ← reader.validateUnique("id", id)
    } yield {
      val w = newPropertyMap
      w += "kind" -> kind
      widgetMap += id -> w
    }
}