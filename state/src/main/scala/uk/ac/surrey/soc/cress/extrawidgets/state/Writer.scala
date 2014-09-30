package uk.ac.surrey.soc.cress.extrawidgets.state

import org.nlogo.api.SimpleChangeEventPublisher

/**
 *  This is the only class that should <em>ever</em> write to the MutableWidgetMap.
 */
class Writer(
  widgetMap: MutableWidgetMap,
  publisher: SimpleChangeEventPublisher,
  reader: Reader) {

  def add(kind: WidgetKind, id: WidgetID): Either[String, Unit] =
    for {
      _ ← reader.validateNonEmpty("id", id).right
      _ ← reader.validateUnique("id", id).right
    } yield {
      val w = newPropertyMap
      w += "kind" -> kind
      widgetMap += id -> w
      publisher.publish()
    }

  def remove(id: WidgetID): Unit = {
    widgetMap -= id
    publisher.publish()
  }

}
