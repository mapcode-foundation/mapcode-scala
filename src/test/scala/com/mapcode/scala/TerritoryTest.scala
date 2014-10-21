package com.mapcode.scala

import org.scalatest.{FunSuite, Matchers}

class TerritoryTest extends FunSuite with Matchers {
  test("initialization") {
    Territory.nameMap
  }
  test("toNameFormat") {
    import com.mapcode.scala.NameFormat._

    Territory.MX_DIF.toNameFormat(International) should equal("MX-DIF")
    Territory.MX_DIF.toNameFormat(Minimal) should equal("DIF")
    Territory.MX_MX.toNameFormat(Minimal) should equal("MX")
    Territory.MX_MX.toNameFormat(MinimalUnambiguous) should equal("MX-MX")
    Territory.USA.toNameFormat(International) should equal("USA")
    Territory.USA.toNameFormat(Minimal) should equal("USA")
    Territory.USA.toNameFormat(MinimalUnambiguous) should equal("USA")
    Territory.HKG.toNameFormat(International) should equal("HKG")
    Territory.HKG.toNameFormat(Minimal) should equal("HKG")
    Territory.HKG.toNameFormat(MinimalUnambiguous) should equal("HKG")
  }

  test("fromString") {
    Territory.fromString("America") should be(None)
    an[IllegalArgumentException] should be thrownBy Territory.fromString("America", Some(Territory.US_MT))
    Territory.fromString("CA", Some(Territory.USA)) should equal(Some(Territory.US_CA))
    Territory.fromString("BR") should equal(Some(Territory.IN_BR))
    Territory.fromString("AS") should equal(Some(Territory.IN_AS))
  }

  test("states") {
    Territory.USA.hasStates should be(true)
    Territory.USA.isState should be(false)
    Territory.US_MT.isState should be(true)
    Territory.US_MT.hasStates should be(false)
  }

  test("fromTerritoryCode") {
    Territory.territories.foreach(t => Territory.fromTerritoryCode(t.id) should equal(Some(t)))
    Territory.fromTerritoryCode(-1) should equal(None)
  }

  test("various names") {
    import Territory._
    import NameFormat._

    territories.foreach { territory =>
      territory.parentTerritory match {
        case None =>
          fromString(territory.toNameFormat(International)) should equal(Some(territory))
          fromString(territory.toNameFormat(MinimalUnambiguous)) should equal(Some(territory))
        case Some(parent) =>
          fromString(territory.toNameFormat(Minimal), Some(parent)) should equal(Some(territory))
      }
    }

    fromString("no such top level location", None) should equal(None)
    fromString("no such state", Some(USA)) should equal(None)


  }


  test("exceptional addName behavior") {
    val myTerritories = new TerritoryOperations {
      val USA1 = Territory(409, "USA", None, Seq("US"), Seq("United States of America", "America"))
      val USA2 = Territory(409, "USA", None, Seq("US"), Seq("United States of America", "America"))
    }

    a[RuntimeException] should be thrownBy myTerritories.fromString("US")
  }

  test("short names hit states, long names hit countries") {
    import Territory._
    fromString("RU") should be(Some(RUS))
    fromString("CN") should be(Some(CHN))
    fromString("AU") should be(Some(AUS))
    fromString("IN") should be(Some(US_IN))
    fromString("BR") should be(Some(IN_BR))
    fromString("AS") should be(Some(IN_AS))
  }
}

