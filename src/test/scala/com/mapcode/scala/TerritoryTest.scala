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
    Territory.fromString("America") should equal(Some(Territory.USA))
    an[IllegalArgumentException] should be thrownBy Territory.fromString("America", Some(Territory.US_MT))
    Territory.fromString("CA", Some(Territory.USA)) should equal(Some(Territory.US_CA))

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
    Territory.territories.foreach { territory =>
      val oldNames = Territory.nameMap.filter(_._2.contains(territory)).map(_._1).toSet
      val newNames = territory.allNames.toSet
      newNames should be(oldNames)
    }

    Territory.territories.foreach { territory =>
      territory.allNames.foreach { name =>
        Territory.fromString(name, territory.parentTerritory) should equal(Some(territory))
      }
    }

    Territory.territories.foreach { territory =>
      Territory.fromString(territory.toNameFormat(NameFormat.International)) should equal(Some(territory))
      Territory.fromString(territory.toNameFormat(NameFormat.MinimalUnambiguous)) should equal(Some(territory))
    }

    Territory.fromString("no such location", None) should equal(None)
    Territory.fromString("no such location", Some(Territory.USA)) should equal(None)
  }


  test("exceptional addName behavior") {
    val myTerritories = new TerritoryOperations {
      val USA1 = Territory(409, "USA", None, Seq("US"), Seq("United States of America", "America"))
      val USA2 = Territory(409, "USA", None, Seq("US"), Seq("United States of America", "America"))
    }

    a[RuntimeException] should be thrownBy myTerritories.fromString("US")
  }
}

