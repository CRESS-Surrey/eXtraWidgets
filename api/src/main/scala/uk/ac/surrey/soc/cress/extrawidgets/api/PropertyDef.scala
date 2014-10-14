package uk.ac.surrey.soc.cress.extrawidgets.api

import org.nlogo.api.Syntax._
import org.nlogo.api.Color._
import org.nlogo.api.LogoList
import org.nlogo.agent.Agent._
import org.nlogo.api.I18N

abstract class PropertyDef[+W <: ExtraWidget, T](
  val widget: W,
  val setter: T ⇒ Unit,
  val getter: () ⇒ T,
  val default: () ⇒ T) {
  val inputTypeConstant: Int
  val outputTypeConstant: Int
  def asInputType(obj: AnyRef): T = obj.asInstanceOf[T] // TODO: handle this
  private def set(v: T): Unit = if (getter() != v) setter(v)
  def setValue(obj: AnyRef): Unit = set(asInputType(obj))
  def setToDefault(): Unit = set(default())
}

class StringPropertyDef[+W <: ExtraWidget](
  w: W,
  setter: String ⇒ Unit,
  getter: () ⇒ String,
  default: () ⇒ String)
  extends PropertyDef(w, setter, getter, default) {
  val inputTypeConstant = StringType
  val outputTypeConstant = StringType
  override def asInputType(obj: AnyRef): String = obj.toString
}

class BooleanPropertyDef[+W <: ExtraWidget](
  w: W,
  setter: java.lang.Boolean ⇒ Unit,
  getter: () ⇒ java.lang.Boolean,
  default: () ⇒ java.lang.Boolean)
  extends PropertyDef(w, setter, getter, default) {
  val inputTypeConstant = BooleanType
  val outputTypeConstant = BooleanType
}

class IntegerPropertyDef[+W <: ExtraWidget](
  w: W,
  setter: java.lang.Integer ⇒ Unit,
  getter: () ⇒ java.lang.Integer,
  default: () ⇒ java.lang.Integer)
  extends PropertyDef(w, setter, getter, default) {
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
  getter: () ⇒ java.awt.Color,
  default: () ⇒ java.awt.Color)
  extends PropertyDef(w, setter, getter, default) {
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
   * Not sure if the Either stuff is the paramount of elegance
   * or just insanely obscure and complicated. Probably the latter.
   * NP 2014-10-13.
   */
  def validRGBList(rgb: Vector[Any]): LogoList =
    Either.cond(Set(3, 4).contains(rgb.size), rgb: Vector[Any],
      new XWException(I18N.errors.get("org.nlogo.agent.Agent.rgbListSizeError.3or4"))
    ).right.flatMap { rgb ⇒
        rgb.map { x ⇒
          tryTo(x.asInstanceOf[java.lang.Number].intValue).right.flatMap { c ⇒
            Either.cond(c < 0 || c > 255, c,
              new XWException(I18N.errors.get("org.nlogo.agent.Agent.rgbValueError")))
          }
        }.foldLeft(Right(LogoList.Empty): Either[XWException, LogoList]) {
          case (acc, x) ⇒
            acc.fold(e ⇒ Left(e), ll ⇒ x.fold(
              e ⇒ Left(e),
              c ⇒ Right(ll.lput(c: java.lang.Integer))))
        }
      }
      .fold(e ⇒ throw e, identity)
}
