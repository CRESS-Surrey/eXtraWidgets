package uk.ac.surrey.soc.cress.extrawidgets.state

import org.nlogo.api.SimpleChangeEventPublisher

/**
 *  This is the only class that should <em>ever</em> write to the MutableWidgetMap.
 */
class Writer(
  widgetMap: MutableWidgetMap,
  publisher: SimpleChangeEventPublisher,
  reader: Reader) {

  def add(kind: WidgetKind, widgetKey: WidgetKey): Either[String, Unit] =
    for {
      _ ← reader.validateNonEmpty("widget key", widgetKey).right
      _ ← reader.validateUnique("widget key", widgetKey).right
    } yield {
      val w = newPropertyMap
      w += "kind" -> kind
      widgetMap += widgetKey -> w
      publisher.publish()
    }

  def remove(widgetKey: WidgetKey): Unit = {
    widgetMap -= widgetKey
    publisher.publish()
  }

}
