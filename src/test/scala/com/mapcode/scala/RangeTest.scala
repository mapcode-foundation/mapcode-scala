package com.mapcode.scala

import org.scalatest.prop.{Configuration, GeneratorDrivenPropertyChecks}
import org.scalatest.{Matchers, FunSuite}

class RangeTest extends FunSuite with Matchers with GeneratorDrivenPropertyChecks {

  implicit override val generatorDrivenConfig = PropertyCheckConfig(maxDiscarded = 200)

  test("range") {
    forAll {
      (a: Int, b: Int, c: Int, d: Int) =>
        whenever(a != b && a != c && a != d && b != c && b != d && c != d) {
          val sorted = Seq(a, b, c, d).sorted
          Range[Int](sorted(0), sorted(1)).containsRange(Range[Int](sorted(2), sorted(3))) should be(false)
          Range[Int](sorted(0), sorted(2)).containsRange(Range[Int](sorted(1), sorted(3))) should be(false)
          Range[Int](sorted(0), sorted(3)).containsRange(Range[Int](sorted(1), sorted(2))) should be(true)
        }
    }
  }

  test("intersection") {
    forAll {
      (a: Int, b: Int, c: Int, d: Int) =>
        whenever(a != b && a != c && a != d && b != c && b != d && c != d) {
          val sorted = Seq(a, b, c, d).sorted
          Range[Int](sorted(0), sorted(1)).intersects(Range[Int](sorted(2), sorted(3))) should be(false)
          Range[Int](sorted(0), sorted(2)).intersects(Range[Int](sorted(1), sorted(3))) should be(true)
          Range[Int](sorted(0), sorted(3)).intersects(Range[Int](sorted(1), sorted(2))) should be(true)
        }
    }
  }

  test("constrain") {
    forAll {
      (a: Int, b: Int, c: Int, d: Int) =>
        whenever(a != b && a != c && a != d && b != c && b != d && c != d) {
          val sorted = Seq(a, b, c, d).sorted
          Range[Int](sorted(0), sorted(1)).constrain(Range[Int](sorted(2), sorted(3))) should be(None)
          Range[Int](sorted(2), sorted(3)).constrain(Range[Int](sorted(0), sorted(1))) should be(None)
          Range[Int](sorted(0), sorted(2)).constrain(Range[Int](sorted(1), sorted(3))) should be(Some(Range[Int](sorted(1), sorted(2))))
          Range[Int](sorted(1), sorted(3)).constrain(Range[Int](sorted(0), sorted(2))) should be(Some(Range[Int](sorted(1), sorted(2))))
          Range[Int](sorted(0), sorted(3)).constrain(Range[Int](sorted(1), sorted(2))) should be(Some(Range[Int](sorted(1), sorted(2))))
          Range[Int](sorted(1), sorted(2)).constrain(Range[Int](sorted(0), sorted(3))) should be(Some(Range[Int](sorted(1), sorted(2))))
        }
    }
  }

  test("constrainAll") {
    val testRange = Range[Int](0, 10)
    val constraints = Seq(Range[Int](0, 1), Range[Int](1, 2), Range[Int](2, 3), Range[Int](-1, 0))
    testRange.constrainAll(constraints) should equal(constraints.init)
  }
}
