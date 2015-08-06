package com.mapcode.scala

import org.scalatest.{FunSuite, Matchers}

class TerritoryTest extends FunSuite with Matchers {
  test("toAlphaCode") {
    import com.mapcode.scala.Territory.AlphaCodeFormat._

    Territory.MX_DIF.toAlphaCode(INTERNATIONAL) should equal("MX-DIF")
    Territory.MX_DIF.toAlphaCode(MINIMAL) should equal("DIF")
    Territory.MX_MX.toAlphaCode(MINIMAL) should equal("MX")
    Territory.MX_MX.toAlphaCode(MINIMAL_UNAMBIGUOUS) should equal("MX-MX")
    Territory.USA.toAlphaCode(INTERNATIONAL) should equal("USA")
    Territory.USA.toAlphaCode(MINIMAL) should equal("USA")
    Territory.USA.toAlphaCode(MINIMAL_UNAMBIGUOUS) should equal("USA")
    Territory.HKG.toAlphaCode(INTERNATIONAL) should equal("HKG")
    Territory.HKG.toAlphaCode(MINIMAL) should equal("HKG")
    Territory.HKG.toAlphaCode(MINIMAL_UNAMBIGUOUS) should equal("HKG")
  }

  test("fromString") {
    Territory.fromString("America") should be(Territory.USA)
    an[IllegalArgumentException] should be thrownBy Territory.fromString("America", Territory.USA)
    Territory.fromString("CA", Territory.USA) should equal(Territory.US_CA)
    Territory.fromString("BR") should equal(Territory.IN_BR)
    Territory.fromString("AS") should equal(Territory.IN_AS)
  }

  test("states") {
    Territory.USA shouldBe 'hasSubdivisions
    Territory.USA should not be 'isSubdivision
    Territory.US_MT shouldBe 'isSubdivision
    Territory.US_MT should not be 'hasSubdivisions
  }

  test("various names") {
    import com.mapcode.scala.Territory._

    Territory.values.foreach { territory =>
      territory.parentTerritory match {
        case None =>
          fromString(territory.toAlphaCode(Territory.AlphaCodeFormat.INTERNATIONAL)) should equal(territory)
          fromString(territory.toAlphaCode(Territory.AlphaCodeFormat.MINIMAL_UNAMBIGUOUS)) should equal(territory)
        case Some(parent) =>
          fromString(territory.toAlphaCode(Territory.AlphaCodeFormat.MINIMAL), parent) should equal(territory)
      }
    }
  }

  test("short names hit states, long names hit countries") {
    import com.mapcode.scala.Territory._
    fromString("RU") should be(RUS)
    fromString("CN") should be(CHN)
    fromString("AU") should be(AUS)
    fromString("IN") should be(US_IN)
    fromString("BR") should be(IN_BR)
    fromString("AS") should be(IN_AS)
  }

  test("disambiguateMNTest1") {
    val territory1 = Territory.fromString("IN-UK")
    val territory2 = Territory.fromString("IN-UT", Territory.IND)
    territory1 should be(territory2)
  }

  test("territoryFromStringTest") {
    Territory.fromString("NLD") should be(Territory.NLD)
    Territory.fromString("ARG") should be(Territory.ARG)
    Territory.fromString("ARG") should be(Territory.ARG)
    Territory.fromString("US-AS") should be(Territory.ASM)
    Territory.fromString("USA-AS") should be(Territory.ASM)
    Territory.fromString("RU") should be(Territory.RUS)
    Territory.fromString("AU") should be(Territory.AUS)
    Territory.fromString("IN") should be(Territory.US_IN)
    Territory.fromString("BR") should be(Territory.IN_BR)
    Territory.fromString("AS") should be(Territory.IN_AS)
  }
}

