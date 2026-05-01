package uk.ac.surrey.xw.extension

import org.nlogo.api.ExtensionException
import org.nlogo.core.LogoList
import org.scalatest.funsuite.AnyFunSuite

import uk.ac.surrey.xw.extension.util.*

class UtilTests extends AnyFunSuite {

  test("toPropertyMap rejects entries that are not lists") {
    val exception = intercept[ExtensionException] {
      Vector[AnyRef]("not a list").toPropertyMap
    }

    assert(exception.getMessage.contains("not a list is not a list."))
  }

  test("toPropertyMap rejects entries without a value") {
    val exception = intercept[ExtensionException] {
      Vector[AnyRef](LogoList.fromVector(Vector[AnyRef]("KEY"))).toPropertyMap
    }

    assert(exception.getMessage.contains("[KEY] does not contain two elements."))
  }

  test("toPropertyMap rejects entries whose key is not a string") {
    val exception = intercept[ExtensionException] {
      Vector[AnyRef](LogoList.fromVector(Vector[AnyRef](Double.box(1), "value"))).toPropertyMap
    }

    assert(exception.getMessage.contains("Trying to use 1 as a key, but it is not a string."))
  }
}
