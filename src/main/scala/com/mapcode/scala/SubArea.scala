/*
 * Copyright (C) 2014 Stichting Mapcode Foundation (http://www.mapcode.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mapcode.scala

import java.util

import scala.collection.mutable.ArrayBuffer
import scala.util.Try

/**
 * ----------------------------------------------------------------------------------------------
 * Package private implementation class. For internal use within the mapcode implementation only.
 * ----------------------------------------------------------------------------------------------
 *
 * This class contains a class that defines an area for local mapcodes.
 */
private[scala] case class SubArea(latRange: Range,
                                  lonRange: Range,
                                  boundedLatRange: Seq[Range],
                                  boundedLonRange: Seq[Range],
                                  parentTerritory: Territory.Territory,
                                  subAreaId: Int) {

  def minX: Int = lonRange.min

  def minY: Int = latRange.min

  def maxX: Int = lonRange.max

  def maxY: Int = latRange.max

  def containsPoint(point: Point): Boolean =
    latRange.contains(point.latMicroDeg) && containsLongitude(point.lonMicroDeg)

  def extendBounds(xExtension: Int, yExtension: Int): SubArea = {
    this.copy(
      latRange = Range(this.minY - yExtension, maxY + yExtension),
      lonRange = Range(this.minX - xExtension, maxX + xExtension)
    )
  }

  def containsLongitude(lonMicroDeg: Int): Boolean = {
    this.lonRange.contains(lonMicroDeg) ||
      this.lonRange.contains(if (lonMicroDeg < lonRange.min) lonMicroDeg + 360000000 else lonMicroDeg - 360000000)
  }
}

private[scala] object SubArea {

  val latBoundingRange = Range(Point.LAT_MICRODEG_MIN, Point.LAT_MICRODEG_MAX)
  val lonBoundingRange = Range(Point.LON_MICRODEG_MIN, Point.LON_MICRODEG_MAX)

  // scala doesn't have a good mutable TreeMap that has the range features
  // we need, so we just use it with some sugar to avoid having to fill
  // it eagerly
  class SubAreaMap extends util.TreeMap[Int, ArrayBuffer[SubArea]] {
    def get(key: Int): ArrayBuffer[SubArea] = {
      val existing = super.get(key)
      if (existing == null) {
        val buf = ArrayBuffer[SubArea]()
        super.put(key.asInstanceOf[Int], buf)
        buf
      } else existing
    }
  }

  private val (lonMap: SubAreaMap, latMap: SubAreaMap, subAreas: Seq[SubArea]) = {
    import com.mapcode.scala.Territory.territories
    val latMap = new SubAreaMap
    val lonMap = new SubAreaMap
    val subs = new Array[SubArea](territories.map(t => DataAccess.dataLastRecord(t.territoryCode)).max + 1)
    for (territory <- territories) {
      import territory.territoryCode
      val first = DataAccess.dataFirstRecord(territoryCode)
      val last = DataAccess.dataLastRecord(territoryCode)
      for (i <- last to first by -1) {
        assert(subs(i) == null)
        subs(i) = SubArea(i, territory, Option(subs(last)))
      }
    }
    assert(!subs.contains(null), s"subs should be fully populated: $subs")
    import scala.collection.JavaConverters._
    for {
      subArea <- subs if subArea.boundedLatRange.nonEmpty && subArea.boundedLonRange.nonEmpty
    } {
      for {
        lonRange <- subArea.boundedLonRange
        key <- lonMap.subMap(lonRange.min, lonRange.max + 1).keySet().asScala
      } lonMap.get(key) += subArea
      for {
        latRange <- subArea.boundedLatRange
        key <- latMap.subMap(latRange.min, latRange.max + 1).keySet().asScala
      } latMap.get(key) += subArea
    }

    (lonMap, latMap, subs.toIndexedSeq)
  }

  def getArea(i: Int): Option[SubArea] = Try(subAreas(i)).toOption

  def getAreasForPoint(point: Point): Seq[SubArea] = {
    def findRange(map: SubAreaMap, index: Int): Seq[Seq[SubArea]] = {
      map.get(index) match {
        case null =>
          (map.lowerEntry(index), map.higherEntry(index)) match {
            case (lower, higher) if lower == null || higher == null => Seq.empty
            case (lower, higher) => Seq(lower.getValue, higher.getValue)
          }
        case seq => Seq(seq)
      }
    }

    val allAreas = findRange(latMap, point.latMicroDeg) match {
      case range if range.isEmpty => Seq.empty
      case range => range ++ findRange(lonMap, point.lonMicroDeg)
    }

    if (allAreas.nonEmpty) {
      for {
        subArea <- allAreas(0) if allAreas.tail.exists(_.contains(subArea))
      } yield subArea
    } else Seq.empty
  }

  private def normaliseRange(range: Range, boundingRange: Range): Seq[Range] = {
    val ranges = new ArrayBuffer[Range]
    ranges ++= range.constrain(boundingRange)
    var normalisingRange = range
    while (normalisingRange.min < boundingRange.min) {
      normalisingRange = new Range(normalisingRange.min + boundingRange.max - boundingRange.min, normalisingRange.max + boundingRange.max - boundingRange.min)
      ranges ++= normalisingRange.constrain(boundingRange)
    }
    normalisingRange = range
    while (normalisingRange.max > boundingRange.max) {
      normalisingRange = new Range(normalisingRange.min - boundingRange.max + boundingRange.min, normalisingRange.max - boundingRange.max + boundingRange.min)
      ranges ++= normalisingRange.constrain(boundingRange)
    }
    ranges.toSeq
  }

  private def trimRange(range: Range): Range = Range(range.min, range.max - 1)


  def apply(index: Int, parentTerritory: Territory.Territory, territoryBounds: Option[SubArea]): SubArea = {
    val i = index * 20
    val minX = DataAccess.asLong(i)
    val minY = DataAccess.asLong(i + 4)
    val maxX = DataAccess.asLong(i + 8)
    val maxY = DataAccess.asLong(i + 12)
    val subAreaId = i + 12
    val latRange = Range(minY, maxY)
    val lonRange = Range(minX, maxX)
    val trimmedLonRange = trimRange(lonRange)
    val trimmedLatRange = if (latRange.max != 90000000) {
      trimRange(latRange)
    } else latRange
    val (normalisedLatRange, normalisedLonRange) =
      (normaliseRange(trimmedLatRange, latBoundingRange),
        normaliseRange(trimmedLonRange, lonBoundingRange))

    val (boundedLatRange, boundedLonRange) =
      territoryBounds.fold((normalisedLatRange, normalisedLonRange)) { bounds =>
        (normalisedLatRange.flatMap(_.constrainAll(bounds.boundedLatRange)),
          normalisedLonRange.flatMap(_.constrainAll(bounds.boundedLonRange)))
      }
    new SubArea(
      latRange = latRange,
      lonRange = lonRange,
      boundedLatRange = boundedLatRange,
      boundedLonRange = boundedLonRange,
      parentTerritory = parentTerritory,
      subAreaId = subAreaId)
  }
}

