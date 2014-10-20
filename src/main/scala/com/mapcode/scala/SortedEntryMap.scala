package com.mapcode.scala

import java.util

case class SortedEntryMap[T](map: Map[Int, T]) {

  private val sortedKeys = map.keys.toSeq.sorted.toArray

  def apply(key: Int): T = map(key)
  def get(key: Int): Option[T] = map.get(key)

  /** @return an entry with the greatest key less than key */
  def lowerEntry(key: Int): Option[T] = {
    val foundIndex = util.Arrays.binarySearch(sortedKeys, key)
    foundIndex match {
      case exact if exact > 0 => map.get(sortedKeys(exact - 1))
      case exact if exact == 0 => None
      case minusInsertionPointMinus1 =>
        val insertionPoint = -(minusInsertionPointMinus1 + 1)
        if (insertionPoint == sortedKeys.size) map.get(sortedKeys.last)
        else {
          val idx = insertionPoint - 1
          if (idx >= 0) map.get(sortedKeys(idx))
          else None
        }
    }
  }

  /** @return an entry with the greatest key greater than key */
  def higherEntry(key: Int): Option[T] = {
    val foundIndex = util.Arrays.binarySearch(sortedKeys, key)
    foundIndex match {
      case exact if exact == sortedKeys.size - 1 => None
      case exact if exact >= 0 => map.get(sortedKeys(exact + 1))
      case minusInsertionPointMinus1 =>
        val insertionPoint = -(minusInsertionPointMinus1 + 1)
        if (insertionPoint == sortedKeys.size) None
        else {
          map.get(sortedKeys(insertionPoint))
        }
    }
  }
}
