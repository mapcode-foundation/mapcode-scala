package com.mapcode.scala

import org.scalacheck.Arbitrary
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSuite, Matchers}

class CheckArgsTest extends FunSuite with Matchers with GeneratorDrivenPropertyChecks {

  implicit val rangeGen = for {
    a <- Arbitrary.arbDouble.arbitrary
    b <- Arbitrary.arbDouble.arbitrary if a != b
  } yield if (a < b) (a, b) else (b, a)

  test("test underflow") {
    forAll {
      (value: Double, range: (Double, Double)) => {
        whenever(value < range._1) {
          an[IllegalArgumentException] should be thrownBy CheckArgs.checkRange("", value, range._1, range._2)
        }
      }
    }
  }
  test("test overflow") {
    forAll {
      (value: Double, range: (Double, Double)) => {
        whenever(value > range._2) {
          an[IllegalArgumentException] should be thrownBy CheckArgs.checkRange("", value, range._1, range._2)
        }
      }
    }
  }

  test("test intersection") {
    forAll {
      (value: Double, range: (Double, Double)) => {
        whenever(value >= range._1 && value <= range._2) {
          CheckArgs.checkRange("", value, range._1, range._2) should be(Unit)
        }
      }
    }
  }
}
