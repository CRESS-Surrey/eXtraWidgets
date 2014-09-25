package uk.ac.surrey.soc.cress.extrawidgets.plugin.model

import org.nlogo.api.SimpleChangeEventPublisher

import uk.ac.surrey.soc.cress.extrawidgets.plugin.util.eitherToRightBiased

/**
 *  This is the only class that should <em>ever</em> write to the MutableWidgetMap.
 */
class Writer(
  widgetMap: MutableWidgetMap,
  publisher: SimpleChangeEventPublisher,
  reader: Reader) {
  def add(kind: WidgetKind, id: WidgetID): Either[String, Unit] =
    for {
      _ ← reader.validateNonEmpty("id", id)
      _ ← reader.validateUnique("id", id)
    } yield {
      val w = newPropertyMap
      w += "kind" -> kind
      widgetMap += id -> w
      publisher.publish()
    }
}