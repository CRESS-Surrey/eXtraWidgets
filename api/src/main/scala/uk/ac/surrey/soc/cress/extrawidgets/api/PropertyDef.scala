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

abstract class PropertyDef[+W <: ExtraWidget, T <: AnyRef](
  val widget: W,
  setter: T ⇒ Unit, // should never be directly accessed from the outside
  val getter: () ⇒ T) {
  val inputTypeConstant: Int
  val outputTypeConstant: Int
  protected def asInputType(obj: AnyRef): T = obj.asInstanceOf[T]
  def updateInState(): Unit = widget.updatePropertyInState(this)
  def setValue(obj: AnyRef): Unit = {
    setter(asInputType(obj))
    updateInState()
  }
  def stringValue =
    Dump.logoObject(getter() match {
      case i: java.lang.Integer ⇒   // `Dump` doesn't like Integer
        Double.box(i.doubleValue()) // so we give it a Double
      case x: AnyRef ⇒ x
    })
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
  override def asInputType(obj: AnyRef): String = obj.toString
}

class BooleanPropertyDef[+W <: ExtraWidget](
  w: W,
  setter: java.lang.Boolean ⇒ Unit,
  getter: () ⇒ java.lang.Boolean)
  extends PropertyDef(w, setter, getter) {
  val inputTypeConstant = BooleanType
  val outputTypeConstant = BooleanType
}

class IntegerPropertyDef[+W <: ExtraWidget](
  w: W,
  setter: java.lang.Integer ⇒ Unit,
  getter: () ⇒ java.lang.Integer)
  extends PropertyDef(w, setter, getter) {
  val inputTypeConstant = NumberType
  val outputTypeConstant = NumberType
  override def asInputType(obj: AnyRef): java.lang.Integer = obj match {
    case d: java.lang.Double ⇒ d.intValue
    case _ ⇒ super.asInputType(obj)
  }
}

class ColorPropertyDef[+W <: ExtraWidget](
  w: W,
  setter: java.awt.Color ⇒ Unit,
  getter: () ⇒ java.awt.Color)
  extends PropertyDef(w, setter, getter) {
  val inputTypeConstant = NumberType | ListType
  val outputTypeConstant = ListType
  override def asInputType(obj: AnyRef): java.awt.Color = obj match {
    case c: java.lang.Double ⇒
      getColor(
        if (c >= 0 || c < MaxColor) c
        else Double.box(modulateDouble(c))
      )
    case v: Vector[_] ⇒ getColor(validRGBList(v))
    case ll: LogoList ⇒ getColor(validRGBList(ll.toVector))
    case _ ⇒ super.asInputType(obj)
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
  override def asInputType(obj: AnyRef): LogoList = obj match {
    case xs: Vector[_] ⇒
      LogoList.fromVector(xs.asInstanceOf[Vector[AnyRef]])
    case xs: java.lang.Iterable[_] ⇒
      LogoList.fromJava(xs.asInstanceOf[java.lang.Iterable[AnyRef]])
    case xs: Seq[_] ⇒
      LogoList(xs.asInstanceOf[Seq[AnyRef]]: _*)
    case it: Iterator[_] ⇒
      LogoList.fromIterator(it.asInstanceOf[Iterator[AnyRef]])
    case _ ⇒ super.asInputType(obj)
  }
}
