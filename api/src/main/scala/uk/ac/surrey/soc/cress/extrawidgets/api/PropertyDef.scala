package uk.ac.surrey.soc.cress.extrawidgets.api

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

abstract class PropertyDef[+W <: ExtraWidget, T](
  val widget: W,
  setter: T ⇒ Unit, // should never be directly accessed from the outside
  val getter: () ⇒ T) {
  val inputTypeConstant: Int
  protected def fromInputType(x: Any): T = x.asInstanceOf[T]
  val outputTypeConstant: Int
  def toOutputType: AnyRef = getter().asInstanceOf[AnyRef]
  def updateInState(): Unit = widget.updatePropertyInState(this)
  def setValue(value: Any): Unit = {
    setter(fromInputType(value))
    updateInState()
  }
  override def toString = Dump.logoObject(toOutputType)
}

class ObjectPropertyDef[+W <: ExtraWidget](
  w: W,
  setter: AnyRef ⇒ Unit,
  getter: () ⇒ AnyRef)
  extends PropertyDef(w, setter, getter) {
  val inputTypeConstant = WildcardType
  val outputTypeConstant = WildcardType
}

class StringPropertyDef[+W <: ExtraWidget](
  w: W,
  setter: String ⇒ Unit,
  getter: () ⇒ String)
  extends PropertyDef(w, setter, getter) {
  val inputTypeConstant = StringType
  val outputTypeConstant = StringType
}

class BooleanPropertyDef[+W <: ExtraWidget](
  w: W,
  setter: Boolean ⇒ Unit,
  getter: () ⇒ Boolean)
  extends PropertyDef(w, setter, getter) {
  val inputTypeConstant = BooleanType
  val outputTypeConstant = BooleanType
}

class IntegerPropertyDef[+W <: ExtraWidget](
  w: W,
  setter: Int ⇒ Unit,
  getter: () ⇒ Int)
  extends PropertyDef(w, setter, getter) {
  val inputTypeConstant = NumberType
  val outputTypeConstant = NumberType
  override def fromInputType(x: Any): Int = x match {
    case d: java.lang.Double ⇒ d.intValue
    case _ ⇒ super.fromInputType(x)
  }
  override def toOutputType = Double.box(getter().toDouble)
}

class DoublePropertyDef[+W <: ExtraWidget](
  w: W,
  setter: Double ⇒ Unit,
  getter: () ⇒ Double)
  extends PropertyDef(w, setter, getter) {
  val inputTypeConstant = NumberType
  val outputTypeConstant = NumberType
}

class ColorPropertyDef[+W <: ExtraWidget](
  w: W,
  setter: java.awt.Color ⇒ Unit,
  getter: () ⇒ java.awt.Color)
  extends PropertyDef(w, setter, getter) {
  val inputTypeConstant = NumberType | ListType
  val outputTypeConstant = ListType
  override def fromInputType(x: Any): java.awt.Color = {
    x match {
      case c: java.lang.Double ⇒
        getColor(
          if (c >= 0 || c < MaxColor) c
          else Double.box(modulateDouble(c))
        )
      case ll: LogoList ⇒ getColor(validRGBList(ll.toVector))
      case _ ⇒ super.fromInputType(x)
    }
  }

  override def toOutputType: AnyRef = {
    val c = getter()
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

class ListPropertyDef[+W <: ExtraWidget](
  w: W,
  setter: LogoList ⇒ Unit,
  getter: () ⇒ LogoList)
  extends PropertyDef(w, setter, getter) {
  val inputTypeConstant = ListType
  val outputTypeConstant = ListType
}
