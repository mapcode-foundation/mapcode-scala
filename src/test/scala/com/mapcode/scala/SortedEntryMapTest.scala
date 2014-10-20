package com.mapcode.scala

import org.scalatest.{Matchers, FunSuite}

class SortedEntryMapTest extends FunSuite with Matchers {

  test("basics") {
    val map = Map(1 -> "one", 2 -> "two", 4 -> "four", 6 -> "six")
    val sortedMap = SortedEntryMap(map)

    sortedMap(1) should equal("one")
    sortedMap.get(2) should equal(Some("two"))
    sortedMap.get(3) should equal(None)
    a[NoSuchElementException] should be thrownBy sortedMap(3)

    sortedMap.lowerEntry(0) should be(None)
    sortedMap.lowerEntry(1) should be(None)
    sortedMap.lowerEntry(2) should be(Some("one"))
    sortedMap.lowerEntry(3) should be(Some("two"))
    sortedMap.lowerEntry(4) should be(Some("two"))
    sortedMap.lowerEntry(5) should be(Some("four"))
    sortedMap.lowerEntry(6) should be(Some("four"))
    sortedMap.lowerEntry(7) should be(Some("six"))

    sortedMap.higherEntry(0) should be(Some("one"))
    sortedMap.higherEntry(1) should be(Some("two"))
    sortedMap.higherEntry(2) should be(Some("four"))
    sortedMap.higherEntry(3) should be(Some("four"))
    sortedMap.higherEntry(4) should be(Some("six"))
    sortedMap.higherEntry(5) should be(Some("six"))
    sortedMap.higherEntry(6) should be(None)
    sortedMap.higherEntry(7) should be(None)
  }
}
