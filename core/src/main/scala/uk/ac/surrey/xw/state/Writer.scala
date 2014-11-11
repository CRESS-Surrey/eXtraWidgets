package uk.ac.surrey.xw.state

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.collection.JavaConverters.mapAsScalaMapConverter
import scala.collection.mutable.Publisher

import org.json.simple.parser.JSONParser
import org.json.simple.parser.ParseException
import org.nlogo.api.Dump
import org.nlogo.api.LogoList

import uk.ac.surrey.xw.api.PropertyKey
import uk.ac.surrey.xw.api.PropertyMap
import uk.ac.surrey.xw.api.PropertyValue
import uk.ac.surrey.xw.api.StateUpdater
import uk.ac.surrey.xw.api.WidgetKey
import uk.ac.surrey.xw.api.XWException
import uk.ac.surrey.xw.api.enrichEither
import uk.ac.surrey.xw.api.normalizeString

/**
 *  This is the only class that should <em>ever</em> write to the MutableWidgetMap.
 */
class Writer(
  widgetMap: MutableWidgetMap,
  val reader: Reader)
  extends Publisher[StateEvent]
  with StateUpdater {

  override type Pub = Publisher[StateEvent]

  def add(widgetKey: WidgetKey, propertyMap: PropertyMap): Unit = {
    val wKey = normalizeString(widgetKey)
    val properties = propertyMap.normalizeKeys
    reader.validateNonEmpty("widget key", wKey).rightOrThrow
    reader.validateUnique("widget key", wKey).rightOrThrow
    widgetMap += wKey -> properties.asMutablePropertyMap
    publish(AddWidget(wKey, properties))
  }

  def remove(widgetKey: WidgetKey): Unit = {
    val wKey = normalizeString(widgetKey)
    widgetMap -= wKey
    publish(RemoveWidget(wKey))
  }

  def set(
    propertyKey: PropertyKey,
    widgetKey: WidgetKey,
    propertyValue: PropertyValue): Unit =
    set(propertyKey, widgetKey, propertyValue, false)

  def set(
    propertyKey: PropertyKey,
    widgetKey: WidgetKey,
    propertyValue: PropertyValue,
    publishEvent: Boolean): Unit = {

    val wKey = normalizeString(widgetKey)
    val propertyMap = widgetMap.getOrElse(wKey,
      throw XWException("Widget " + wKey + " does not exist."))
    val pKey = normalizeString(propertyKey)
    val oldValue = propertyMap.get(pKey)
    if (Some(propertyValue) != oldValue) {
      propertyMap += pKey -> propertyValue
      println("(" + Thread.currentThread().getName() + ") " +
        wKey + "/" + pKey + " := " + propertyValue)
      if (publishEvent) publish(SetProperty(wKey, pKey, propertyValue))
    }
  }

  def clearAll() {
    reader.widgetKeyVector.sortBy { k ⇒ // tabs last
      reader.propertyMap(k).right.toOption
        .flatMap(_.get("KIND"))
        .map(_.toString).map(normalizeString) == Some("TAB")
    }.foreach(remove)
  }

  /**
   * Handle the possible values returned by the JSON
   * parser. Nulls are unsupported for now, and so are
   * other custom object that could, in theory, be
   * add (e.g. nobody, extension objects, etc.)
   */
  def convertJSONValue(v: Any): AnyRef = try v match {
    case l: java.util.List[_] ⇒
      LogoList(l.asScala.map(convertJSONValue): _*)
    case s: java.lang.String ⇒ s
    case n: java.lang.Number ⇒ Double.box(n.doubleValue)
    case b: java.lang.Boolean ⇒ b
  } catch {
    case e: MatchError ⇒ throw XWException(
      "Unsupported value in JSON input: " +
        Dump.logoObject(v.asInstanceOf[AnyRef]), e)
  }

  def loadJSON(json: String): Unit = {
    val widgetMap =
      try new JSONParser().parse(json).asInstanceOf[java.util.Map[_, _]]
      catch {
        case e: ParseException ⇒ throw XWException(
          "Error parsing JSON input at position " + e.getPosition, e)
        case e: ClassCastException ⇒ throw XWException(
          "Error parsing JSON input: main value is not a JSON object.", e)
      }
    for {
      (widgetKey: String, jMap: java.util.Map[_, _]) ← widgetMap.asScala
      propertyMap = jMap.asScala.map {
        case (k: String, v) ⇒ k -> convertJSONValue(v)
      }
    } add(widgetKey, propertyMap.toMap)
  }
}
