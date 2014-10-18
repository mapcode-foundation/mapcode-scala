package com.mapcode.scala

import org.scalatest.{FunSuite, Matchers}

class ParentTerritoriesTest extends FunSuite with Matchers {

  test("hard-coded parent territories match raw data") {
    Territory.territories.flatMap(_.parentTerritory).toSet should equal(ParentTerritory.values)
  }
}
