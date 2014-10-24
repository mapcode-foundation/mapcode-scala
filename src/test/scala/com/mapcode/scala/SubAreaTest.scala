package com.mapcode.scala

import org.scalatest.{FunSuite, Matchers}

class SubAreaTest extends FunSuite with Matchers {

  test("SubAreaMap") {
    val map = new SubArea.SubAreaMap
    map.get(0).size should be(0)
    map.get(0) += null
    map.get(0).size should be(1)
  }

  test("equality") {
    // note that subA and subB have the same value, but fail equality. This is because
    // we force equality checks back to references as an optimiztion
    val subA = SubArea(Range(0, 1), Range(2, 3), Seq.empty, Seq.empty, Territory.AAA, 1)
    val subB = SubArea(Range(0, 1), Range(2, 3), Seq.empty, Seq.empty, Territory.AAA, 1)
    subA should not be subB
  }
}
