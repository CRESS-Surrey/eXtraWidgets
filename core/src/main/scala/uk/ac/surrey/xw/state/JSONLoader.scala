package uk.ac.surrey.xw.state

import scala.Left
import scala.Option.option2Iterable
import scala.Right
import scala.collection.JavaConverters.asScalaBufferConverter
import scala.collection.JavaConverters.mapAsScalaMapConverter

import org.nlogo.api.Dump
import org.nlogo.core.LogoList
import org.nlogo.core.Nobody

import uk.ac.surrey.xw.api.XWException

import org.json.simple.Jsoner
import org.json.simple.DeserializationException

class JSONLoader(writer: Writer) {

  // Handle the possible values returned by the JSON parser
  def convertJSONValue(v: Any): AnyRef = try v match {
    case l: java.util.List[_] ⇒
      LogoList(l.asScala.map(convertJSONValue): _*)
    case s: java.lang.String ⇒ s
    case n: java.lang.Number ⇒ Double.box(n.doubleValue)
    case b: java.lang.Boolean ⇒ b
    case null ⇒ Nobody
  } catch {
    case e: MatchError ⇒ throw XWException(
      "Unsupported value in JSON input: " +
        Dump.logoObject(v.asInstanceOf[AnyRef]), e)
  }

  def load(json: String): Unit = {
    val javaWidgetMap =
      try Jsoner.deserialize(json).asInstanceOf[java.util.Map[_, _]]
      catch {
        case e: DeserializationException ⇒ throw XWException(
          "Error parsing JSON input at position " + e.getPosition, e)
        case e: ClassCastException ⇒ throw XWException(
          "Error parsing JSON input: main value is not a JSON object.", e)
      }
    val errors = (for {
      (widgetKey: String, jMap: java.util.Map[_, _]) ← javaWidgetMap.asScala
      propertyMap = jMap.asScala.map {
        case (k: String, v) ⇒ k -> convertJSONValue(v)
        case (k, v) => throw new XWException("Key " + k + " is not a string")
      }
    } yield widgetKey -> propertyMap.toMap)
      .toSeq
      .sortBy(_._2.get("KIND") != Some("TAB"))
      .map {
        case (k, ps) ⇒
          try {
            writer.add(k, ps)
            Right(Unit)
          } catch {
            case e: XWException ⇒ Left(e.getMessage)
          }
      }
      .flatMap(_.left.toOption)
    if (errors.nonEmpty) throw new XWException(errors.mkString("\n"))
  }
}
