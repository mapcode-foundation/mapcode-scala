package com.mapcode.scala

import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSuite, Matchers}

class RangeTest extends FunSuite with Matchers with GeneratorDrivenPropertyChecks {

  implicit override val generatorDrivenConfig = PropertyCheckConfig(maxDiscarded = 200)

  test("range") {
    forAll {
      (a: Int, b: Int, c: Int, d: Int) =>
        whenever(a != b && a != c && a != d && b != c && b != d && c != d) {
          val sorted = Seq(a, b, c, d).sorted
          Range(sorted(0), sorted(1)).containsRange(Range(sorted(2), sorted(3))) shouldBe false
          Range(sorted(0), sorted(2)).containsRange(Range(sorted(1), sorted(3))) shouldBe false
          Range(sorted(0), sorted(3)).containsRange(Range(sorted(1), sorted(2))) shouldBe true
        }
    }
  }

  test("intersection") {
    forAll {
      (a: Int, b: Int, c: Int, d: Int) =>
        whenever(a != b && a != c && a != d && b != c && b != d && c != d) {
          val sorted = Seq(a, b, c, d).sorted
          Range(sorted(0), sorted(1)).intersects(Range(sorted(2), sorted(3))) shouldBe false
          Range(sorted(0), sorted(2)).intersects(Range(sorted(1), sorted(3))) shouldBe true
          Range(sorted(0), sorted(3)).intersects(Range(sorted(1), sorted(2))) shouldBe true
        }
    }
  }

  test("constrain") {
    forAll {
      (a: Int, b: Int, c: Int, d: Int) =>
        whenever(a != b && a != c && a != d && b != c && b != d && c != d) {
          val sorted = Seq(a, b, c, d).sorted
          Range(sorted(0), sorted(1)).constrain(Range(sorted(2), sorted(3))) should be(None)
          Range(sorted(2), sorted(3)).constrain(Range(sorted(0), sorted(1))) should be(None)
          Range(sorted(0), sorted(2)).constrain(Range(sorted(1), sorted(3))) should be(Some(Range(sorted(1), sorted(2))))
          Range(sorted(1), sorted(3)).constrain(Range(sorted(0), sorted(2))) should be(Some(Range(sorted(1), sorted(2))))
          Range(sorted(0), sorted(3)).constrain(Range(sorted(1), sorted(2))) should be(Some(Range(sorted(1), sorted(2))))
          Range(sorted(1), sorted(2)).constrain(Range(sorted(0), sorted(3))) should be(Some(Range(sorted(1), sorted(2))))
        }
    }
  }

  test("constrainAll") {
    val testRange = Range(0, 10)
    val constraints = Seq(Range(0, 1), Range(1, 2), Range(2, 3), Range(-1, 0))
    testRange.constrainAll(constraints) should equal(constraints.init)
  }

  test("out-of-order range blows exception") {
    an[IllegalArgumentException] should be thrownBy Range(2, 1)
  }
}
