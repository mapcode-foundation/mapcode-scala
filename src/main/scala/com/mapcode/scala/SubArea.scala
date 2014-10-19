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

case class SubArea(latRange: Range[Int],
                   lonRange: Range[Int],
                   boundedLatRange: Seq[Range[Int]],
                   boundedLonRange: Seq[Range[Int]],
                   parentTerritory: Territory.Territory,
                   subAreaID: Integer)

  /*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.ArrayList
import java.util.Collections
import java.util.List
import java.util.Map
import java.util.SortedMap
import java.util.TreeMap

import scala.collection.mutable.ArrayBuffer

/**
 * ----------------------------------------------------------------------------------------------
 * Package private implementation class. For internal use within the mapcode implementation only.
 * ----------------------------------------------------------------------------------------------
 *
 * This class contains a class that defines an area for local mapcodes.
 */
object SubArea {
  private[scala] def getArea(i: Int): SubArea = subAreas(i)

  @SuppressWarnings(Array("unchecked"))
  private[scala] def getAreasForPoint(point: Point): Seq[SubArea] = {
    val areaLists: ArrayList[ArrayList[SubArea]] = new ArrayList[ArrayList[SubArea]]
    var list: ArrayList[SubArea] = null
    list = latMap.get(point.latMicroDeg)
    if (list != null) {
      areaLists.add(list)
    }
    else {
      var entry: Map.Entry[Integer, ArrayList[SubArea]] = latMap.lowerEntry(point.latMicroDeg)
      if (entry == null) {
        return Collections.EMPTY_LIST
      }
      list = entry.getValue
      assert(list != null)
      areaLists.add(list)
      entry = latMap.higherEntry(point.latMicroDeg)
      if (entry == null) {
        return Collections.EMPTY_LIST
      }
      list = entry.getValue
      assert(list != null)
      areaLists.add(list)
    }
    list = lonMap.get(point.lonMicroDeg)
    if (list != null) {
      areaLists.add(list)
    }
    else {
      var entry: Map.Entry[Integer, ArrayList[SubArea]] = lonMap.lowerEntry(point.lonMicroDeg)
      if (entry == null) {
        return Collections.EMPTY_LIST
      }
      list = entry.getValue
      assert(list != null)
      areaLists.add(list)
      entry = lonMap.higherEntry(point.lonMicroDeg)
      if (entry == null) {
        return Collections.EMPTY_LIST
      }
      list = entry.getValue
      assert(list != null)
      areaLists.add(list)
    }
    val result: ArrayList[SubArea] = new ArrayList[SubArea]
    list = areaLists.get(0)
    import scala.collection.JavaConversions._
    for (subArea <- list) {
      {
        var i: Int = 1
        while (i < areaLists.size) {
          {
            if (!areaLists.get(i).contains(subArea)) {
              continue //todo: continue is not supported
            }
          }
          ({
            i += 1; i - 1
          })
        }
      }
      result.add(subArea)
    } //todo: labels is not supported
    return result
  }

  private def normaliseRange[Int](range: Range[Int], boundingRange: Range[Int]): Seq[Range[Int]] = {
    val ranges = new ArrayBuffer[Range[Int]]
    ranges ++= range.constrain(boundingRange)
    var normalisingRange = range
    while (normalisingRange.min < boundingRange.min) {
      normalisingRange = new Range[Int](normalisingRange.min + boundingRange.max - boundingRange.min, normalisingRange.max + boundingRange.max - boundingRange.min)
      ranges ++= normalisingRange.constrain(boundingRange)
    }
    normalisingRange = range
    while (normalisingRange.max > boundingRange.max) {
      normalisingRange = new Range[Int](normalisingRange.min - boundingRange.max + boundingRange.min, normalisingRange.max - boundingRange.max + boundingRange.min)
      ranges ++= normalisingRange.constrain(boundingRange)
    }
    ranges.toSeq
  }

  private def trimRange[Int](range: Range[Int]): Range[Int] =  Range[Int](range.min, range.max - 1)

  private final val LOG: Logger = LoggerFactory.logger(classOf[SubArea])
  private final val subAreas: Seq[SubArea] = new Vector[SubArea]
  private final val lonMap: TreeMap[Integer, ArrayList[SubArea]] = new TreeMap[Integer, ArrayList[SubArea]]
  private final val latMap: TreeMap[Integer, ArrayList[SubArea]] = new TreeMap[Integer, ArrayList[SubArea]]
  private final val latBoundingRange[Int]: Range[Int] = new Range[Int](Point.LAT_MICRODEG_MIN, Point.LAT_MICRODEG_MAX)
  private final val lonBoundingRange[Int]: Range[Int] = new Range[Int](Point.LON_MICRODEG_MIN, Point.LON_MICRODEG_MAX)

  def apply(index: Int, territory: Territory.Territory, territoryBounds: SubArea): SubArea = {
    var i = index * 20
    val minX = DataAccess.asLong(i)
    i += 4
    val minY = DataAccess.asLong(i)
    i += 4
    val maxX = DataAccess.asLong(i)
    i += 4
    val maxY = DataAccess.asLong(i)
    val latRange[Int] = Range[Int](minY, maxY)
    val lonRange[Int] = Range[Int](minX, maxX)
    val parentTerritory = territory
    val subAreaID = i
    var boundedLonRange[Int] = Seq.empty[Range[Int]]
    var boundedLatRange[Int] = Seq.empty[Range[Int]]
    val trimmedLonRange[Int] = trimRange[Int](lonRange[Int])
    var trimmedLatRange[Int] = latRange[Int]
    if (latRange[Int].max != 90000000) {
      trimmedLatRange[Int] = trimRange[Int](latRange[Int])
    }
    val normalisedLonRange[Int] = normaliseRange[Int](trimmedLonRange[Int], lonBoundingRange[Int])
    val normalisedLatRange[Int] = normaliseRange[Int](trimmedLatRange[Int], latBoundingRange[Int])
    if (territoryBounds == null) {
      boundedLonRange[Int] = normalisedLonRange[Int]
      boundedLatRange[Int] = normalisedLatRange[Int]
    }
    else {
      import scala.collection.JavaConversions._
      for (normalisedRange[Int] <- normalisedLonRange[Int]) {
        val boundedRange[Int]: ArrayList[Range[Int]] = normalisedRange[Int].constrain(territoryBounds.boundedLonRange[Int])
        if (boundedRange[Int] != null) {
          boundedLonRange[Int].addAll(boundedRange[Int])
        }
      }
      import scala.collection.JavaConversions._
      for (normalisedRange[Int] <- normalisedLatRange[Int]) {
        val boundedRange[Int]: ArrayList[Range[Int]] = normalisedRange[Int].constrain(territoryBounds.boundedLatRange[Int])
        if (boundedRange[Int] != null) {
          boundedLatRange[Int].addAll(boundedRange[Int])
        }
      }
    }
  }
}

case class SubArea(latRange[Int]: Range[Int],
                   lonRange[Int]: Range[Int],
                   boundedLatRange[Int]: Seq[Range[Int]],
                   boundedLonRange[Int]: Seq[Range[Int]],
                   parentTerritory: Territory.Territory,
                   subAreaId: Int) {

  private[scala] def minX: Int =  lonRange[Int].min

  private[scala] def minY: Int =  latRange[Int].min

  private[scala] def maxX: Int =  lonRange[Int].max

  private[scala] def maxY: Int =  latRange[Int].max


  private[scala] def containsPoint(point: Point): Boolean = {
    if (latRange[Int].contains(point.latMicroDeg) && containsLongitude(point.lonMicroDeg)) {
      return true
    }
    return false
  }

  private[scala] def extendBounds(xExtension: Int, yExtension: Int): SubArea = {
    val result: SubArea = new SubArea
    result.latRange[Int] = Range[Int](this.minY - yExtension, maxY + yExtension)
    result.lonRange[Int] = Range[Int](this.minX - xExtension, maxX + xExtension)
    return result
  }

  private[scala] def containsLongitude(argLonMicroDeg: Int): Boolean = {
    var lonMicroDeg: Int = argLonMicroDeg
    if (this.lonRange[Int].contains(lonMicroDeg)) {
      return true
    }
    if (lonMicroDeg < lonRange[Int].min) {
      lonMicroDeg += 360000000
    }
    else {
      lonMicroDeg -= 360000000
    }
    if (this.lonRange[Int].contains(lonMicroDeg)) {
      return true
    }
    return false
  }

  private def minMaxSetup(arg: Int) {
    var i: Int = arg * 20
    val minX: Int = DataAccess.asLong(i)
    i += 4
    val minY: Int = DataAccess.asLong(i)
    i += 4
    val maxX: Int = DataAccess.asLong(i)
    i += 4
    val maxY: Int = DataAccess.asLong(i)
    latRange[Int] = Range[Int](minY, maxY)
    lonRange[Int] = Range[Int](minX, maxX)
  }
}
*/
