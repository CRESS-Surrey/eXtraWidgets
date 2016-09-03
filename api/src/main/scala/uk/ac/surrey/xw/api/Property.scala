package uk.ac.surrey.xw.api

import java.awt.Color

import scala.Vector

import org.nlogo.api.Color.getARGBbyPremodulatedColorNumber
import org.nlogo.api.Color.getClosestColorNumberByARGB
import org.nlogo.api.Color.getColor
import org.nlogo.api.Color.modulateDouble
import org.nlogo.api.Dump
import org.nlogo.core.I18N
import org.nlogo.core.LogoList
import org.nlogo.core.Nobody
import org.nlogo.core.Syntax.BooleanType
import org.nlogo.core.Syntax.ListType
import org.nlogo.core.Syntax.NumberType
import org.nlogo.core.Syntax.StringType
import org.nlogo.core.Syntax.WildcardType

abstract class Property[+T, W](
  _key: PropertyKey,
  setter: Option[(W, T) ⇒ Unit],
  getter: W ⇒ T,
  val defaultValue: T)(implicit m: Manifest[T]) {
  val syntaxType: Int
  val key = makeKey(_key)
  private def checkType(x: Any) = {
    if (!m.runtimeClass.isAssignableFrom(x.getClass))
      throw new IllegalArgumentException(
        "Expected a " + Dump.typeName(m.runtimeClass) + " but got " +
          Dump.logoObject(x.asInstanceOf[AnyRef], true, false) +
          " instead.")
    x
  }
  def decode(x: AnyRef): T = checkType(x).asInstanceOf[T]
  def encode(x: Any): AnyRef = checkType(x).asInstanceOf[AnyRef]
  def get(w: W): AnyRef = getter(w).asInstanceOf[AnyRef]
  def set(w: W, value: AnyRef): Unit = setter.foreach(_(w, decode(value)))
  def readOnly: Boolean = !setter.isDefined
}

class ObjectProperty[W](
  _key: PropertyKey,
  setter: Option[(W, AnyRef) ⇒ Unit],
  getter: W ⇒ AnyRef,
  override val defaultValue: AnyRef = Nobody)
  extends Property(_key, setter, getter, defaultValue) {
  val syntaxType = WildcardType
}

class StringProperty[W](
  _key: PropertyKey,
  setter: Option[(W, String) ⇒ Unit],
  getter: W ⇒ String,
  override val defaultValue: String = "")
  extends Property(_key, setter, getter, defaultValue) {
  val syntaxType = StringType
}

class BooleanProperty[W](
  _key: PropertyKey,
  setter: Option[(W, Boolean) ⇒ Unit],
  getter: W ⇒ Boolean,
  override val defaultValue: Boolean = false)
  extends Property(_key, setter, getter, defaultValue) {
  override val key = {
    val k = makeKey(_key)
    if (k.endsWith("?")) k else k + "?"
  }
  val syntaxType = BooleanType
  override def encode(x: Any): AnyRef = x match {
    case b: Boolean ⇒ Boolean.box(b)
    case _ ⇒ super.encode(x)
  }
  override def decode(x: AnyRef): Boolean = x match {
    case b: java.lang.Boolean ⇒ b.booleanValue
    case _ ⇒ super.decode(x)
  }

}

trait NumberEncoder[T, W] extends Property[T, W] {
  abstract override def encode(x: Any): AnyRef =
    x.asInstanceOf[AnyRef] match {
      case n: java.lang.Number ⇒ Double.box(n.doubleValue)
      case _ ⇒ super.encode(x)
    }
}

class IntegerProperty[W](
  _key: PropertyKey,
  setter: Option[(W, Int) ⇒ Unit],
  getter: W ⇒ Int,
  override val defaultValue: Int = 0)
  extends Property(_key, setter, getter, defaultValue)
  with NumberEncoder[Int, W] {
  val syntaxType = NumberType
  override def decode(x: AnyRef): Int = x match {
    case n: java.lang.Number ⇒ n.intValue
    case _ ⇒ super.decode(x)
  }
  override def get(w: W) = Double.box(getter(w).toDouble)
}

class DoubleProperty[W](
  _key: PropertyKey,
  setter: Option[(W, Double) ⇒ Unit],
  getter: W ⇒ Double,
  override val defaultValue: Double = 0d)
  extends Property(_key, setter, getter, defaultValue)
  with NumberEncoder[Double, W] {
  val syntaxType = NumberType
  override def decode(x: AnyRef): Double = x match {
    case n: java.lang.Number ⇒ n.doubleValue
    case _ ⇒ super.decode(x)
  }
}

class ColorProperty[W](
  _key: PropertyKey,
  setter: Option[(W, Color) ⇒ Unit],
  getter: W ⇒ Color,
  override val defaultValue: Color = Color.white)
  extends Property(_key, setter, getter, defaultValue) {
  val syntaxType = NumberType | ListType
  override def encode(x: Any): AnyRef =
    x.asInstanceOf[AnyRef] match {
      case c: java.awt.Color ⇒ {
        val closestDouble = getClosestColorNumberByARGB(c.getRGB)
        val closestARGB = getARGBbyPremodulatedColorNumber(closestDouble)
        if (closestARGB == c.getRGB)
          Double.box(closestDouble)
        else
          LogoList.fromVector(
            Vector(c.getRed, c.getGreen, c.getBlue, c.getAlpha).map(Double.box(_))
          )
      }
      case n: java.lang.Number ⇒ Double.box(modulateDouble(n.doubleValue))
      case ll: LogoList ⇒ encode(decode(ll))
      case _ ⇒ super.encode(x)
    }
  override def decode(x: AnyRef): java.awt.Color = {
    x match {
      case c: java.lang.Double ⇒ getColor(Double.box(modulateDouble(c)))
      case ll: LogoList ⇒ getColor(validRGBList(ll.toVector))
      case _ ⇒ super.decode(x)
    }
  }

  override def get(w: W): AnyRef = {
    val c = getter(w)
    val rgb = Vector(c.getRed, c.getGreen, c.getBlue)
    val a = c.getAlpha
    val rgba = if (a == 255) rgb else rgb :+ a
    LogoList.fromVector(rgba.map(Double.box(_)))
  }

  /**
   * Throws an error if the rgb list if not a proper list of
   * three or four numbers between 0 and 255.
   * Similar in intent to org.nlogo.Agent.validRGBList.
   * NP 2014-10-13.
   */
  def validRGBList(rgb: Vector[AnyRef]): LogoList = {
    if (!Set(3, 4).contains(rgb.size)) throw XWException(
      I18N.errors.get("org.nlogo.agent.Agent.rgbListSizeError.3or4"))
    LogoList.fromVector(rgb.map { x ⇒
      val c = try x.asInstanceOf[java.lang.Number].intValue catch {
        case e: ClassCastException ⇒ throw XWException(
          "Got " + Dump.logoObject(x) + ", but RGB values must be numbers.")
      }
      if (c < 0 || c > 255) throw XWException(
        I18N.errors.get("org.nlogo.agent.Agent.rgbValueError"))
      Double.box(c.doubleValue)
    })
  }
}

class ListProperty[W](
  _key: PropertyKey,
  setter: Option[(W, LogoList) ⇒ Unit],
  getter: W ⇒ LogoList,
  override val defaultValue: LogoList = LogoList.Empty)
  extends Property(_key, setter, getter, defaultValue) {
  val syntaxType = ListType
}
