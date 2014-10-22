package com.mapcode.scala

import org.scalatest.{FunSuite, Matchers}

class SubAreaTest extends FunSuite with Matchers {

  test("SubAreaMap") {
    val map = new SubArea.SubAreaMap
    map.get(0).size should be(0)
    map.get(0) += null
    map.get(0).size should be(1)
  }
}
