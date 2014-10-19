package com.mapcode.scala

import org.scalatest.{FunSuite, Matchers}

class ParentTerritoriesTest extends FunSuite with Matchers {

  test("hard-coded parent territories match raw data") {
    Territory.parentTerritories.toSet should equal(ParentTerritory.values.toSet)
  }
}
