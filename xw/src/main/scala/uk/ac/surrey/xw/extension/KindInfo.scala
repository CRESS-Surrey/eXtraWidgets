package uk.ac.surrey.xw.extension

import uk.ac.surrey.xw.api.KindName
import uk.ac.surrey.xw.api.PropertyKey
import uk.ac.surrey.xw.api.WidgetKey
import uk.ac.surrey.xw.api.WidgetKind
import uk.ac.surrey.xw.api.XWException
import uk.ac.surrey.xw.state.Reader

class KindInfo(reader: Reader,
  widgetKinds: Map[KindName, WidgetKind[_]]) {

  def kindName(widgetKey: WidgetKey) =
    reader.get("KIND", widgetKey).asInstanceOf[String]
  def kind(kindName: KindName) = {
    widgetKinds.getOrElse(kindName, throw XWException(
      "Unknown widget kind: " + kindName + "."))
  }
  def defaultProperty(widgetKey: WidgetKey) = {
    val _kindName = kindName(widgetKey)
    kind(_kindName).defaultProperty.getOrElse(throw XWException(
      "There is no default property defined for widget kind " + _kindName + "."))
  }
  def property(propertyKey: PropertyKey, widgetKey: WidgetKey) = {
    val _kindName = kindName(widgetKey)
    kind(_kindName).properties.getOrElse(propertyKey, throw new XWException(
      "Property " + propertyKey + " is not available " +
        "for widgets of kind " + _kindName + "."
    ))
  }

}
