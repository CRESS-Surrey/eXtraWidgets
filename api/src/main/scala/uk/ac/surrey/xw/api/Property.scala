package uk.ac.surrey.xw.api

import java.awt.Color
import org.nlogo.api.Color.MaxColor
import org.nlogo.api.Color.getColor
import org.nlogo.api.Color.modulateDouble
import org.nlogo.api.Dump
import org.nlogo.api.I18N
import org.nlogo.api.LogoList
import org.nlogo.api.Syntax.BooleanType
import org.nlogo.api.Syntax.ListType
import org.nlogo.api.Syntax.NumberType
import org.nlogo.api.Syntax.StringType
import org.nlogo.api.Syntax.WildcardType
import org.nlogo.api.Nobody

abstract class Property[+T, W](
  _key: PropertyKey,
  setter: Option[(W, T) ⇒ Unit],
  getter: W ⇒ T,
  val defaultValue: T)(implicit m: Manifest[T]) {
  val inputType: Int
  val outputType: Int
  val key = makeKey(_key)
  def fromAny(x: Any): T = {
    if (!m.erasure.isAssignableFrom(x.getClass))
      throw new IllegalArgumentException(
        "Expected a " + Dump.typeName(m.erasure) + " but got " +
          Dump.logoObject(x.asInstanceOf[AnyRef], true, false) +
          " instead.")
    x.asInstanceOf[T]
  }
  def get(w: W): AnyRef = getter(w).asInstanceOf[AnyRef]
  def set(w: W, value: Any): Unit = setter.foreach(_(w, fromAny(value)))
  def readOnly: Boolean = !setter.isDefined
}

class ObjectProperty[W](
  _key: PropertyKey,
  setter: Option[(W, AnyRef) ⇒ Unit],
  getter: W ⇒ AnyRef,
  override val defaultValue: AnyRef = Nobody)
  extends Property(_key, setter, getter, defaultValue) {
  val inputType = WildcardType
  val outputType = WildcardType
}

class StringProperty[W](
  _key: PropertyKey,
  setter: Option[(W, String) ⇒ Unit],
  getter: W ⇒ String,
  override val defaultValue: String = "")
  extends Property(_key, setter, getter, defaultValue) {
  val inputType = StringType
  val outputType = StringType
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
  val inputType = BooleanType
  val outputType = BooleanType
  override def fromAny(x: Any): Boolean = x match {
    case b: java.lang.Boolean ⇒ b.booleanValue
    case _ ⇒ super.fromAny(x)
  }

}

class IntegerProperty[W](
  _key: PropertyKey,
  setter: Option[(W, Int) ⇒ Unit],
  getter: W ⇒ Int,
  override val defaultValue: Int = 0)
  extends Property(_key, setter, getter, defaultValue) {
  val inputType = NumberType
  val outputType = NumberType
  override def fromAny(x: Any): Int = x match {
    case n: java.lang.Number ⇒ n.intValue
    case _ ⇒ super.fromAny(x)
  }
  override def get(w: W) = Double.box(getter(w).toDouble)
}

class DoubleProperty[W](
  _key: PropertyKey,
  setter: Option[(W, Double) ⇒ Unit],
  getter: W ⇒ Double,
  override val defaultValue: Double = 0d)
  extends Property(_key, setter, getter, defaultValue) {
  val inputType = NumberType
  val outputType = NumberType
  override def fromAny(x: Any): Double = x match {
    case n: java.lang.Number ⇒ n.doubleValue
    case _ ⇒ super.fromAny(x)
  }
}

class ColorProperty[W](
  _key: PropertyKey,
  setter: Option[(W, Color) ⇒ Unit],
  getter: W ⇒ Color,
  override val defaultValue: Color = Color.white)
  extends Property(_key, setter, getter, defaultValue) {
  val inputType = NumberType | ListType
  val outputType = ListType
  override def fromAny(x: Any): java.awt.Color = {
    x match {
      case c: java.lang.Double ⇒
        getColor(
          if (c >= 0 || c < MaxColor) c
          else Double.box(modulateDouble(c))
        )
      case ll: LogoList ⇒ getColor(validRGBList(ll.toVector))
      case _ ⇒ super.fromAny(x)
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
  val inputType = ListType
  val outputType = ListType

}
