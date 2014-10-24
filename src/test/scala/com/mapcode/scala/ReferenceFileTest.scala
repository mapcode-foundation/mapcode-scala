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

import com.mapcode.scala.CheckArgs.checkRange
import org.scalatest.{FunSuite, Matchers}

class ReferenceFileTest extends FunSuite with Matchers {

  import com.mapcode.scala.ReferenceFileTest._

  test("checkRandomReferenceRecords") {
    checkFile(RANDOM_REFERENCE_FILE_1)
    checkFile(RANDOM_REFERENCE_FILE_2)
    checkFile(RANDOM_REFERENCE_FILE_3)
  }

  test("checkGridReferenceRecords") {
    checkFile(GRID_REFERENCE_FILE_1)
    checkFile(GRID_REFERENCE_FILE_2)
    checkFile(GRID_REFERENCE_FILE_3)
  }

  test("checkBoundariesReferenceRecords") {
    checkFile(BOUNDARIES_REFERENCE_FILE)
  }

  test("checkRandomReferenceRecordsPrecision2") {
    checkFile(RANDOM_REFERENCE_FILE_1_HP)
    checkFile(RANDOM_REFERENCE_FILE_2_HP)
    checkFile(RANDOM_REFERENCE_FILE_3_HP)
  }

  test("checkGridReferenceRecordsPrecision2") {
    checkFile(GRID_REFERENCE_FILE_1_HP)
    checkFile(GRID_REFERENCE_FILE_2_HP)
    checkFile(GRID_REFERENCE_FILE_3_HP)
  }

  test("checkBoundariesReferenceRecordsPrecision2") {
    checkFile(BOUNDARIES_REFERENCE_FILE_HP)
  }

  private def checkFile(baseFileName: String) {
    val LOG_LINE_EVERY: Int = 25000
    val METERS_DELTA: Double = 10.0
    var errors = 0
    val chunkIterator = ChunkIterator(baseFileName)
    var count = 1
    var maxDelta = 0d
    def log(msg: => String) = if ((count % LOG_LINE_EVERY) == 0) println(msg)
    for (reference <- chunkIterator) {
      val results = MapcodeCodec.encode(reference.point.latDeg, reference.point.lonDeg)
      log(s"checkFile: #$count, ${chunkIterator.name} (${reference.point}, ${reference.mapcodes} => $results")
      for (result <- results) {
        val found = reference.mapcodes.exists {
          case SimpleMapcode(code, Some(terr)) if terr == result.territory =>
            (code.lastIndexOf('-') > 4 && code == result.mapcodePrecision2) || code == result.mapcode
          case _ => false
        }
        if (!found) {
          println(s"checkFile: Mapcode '$result' at ${reference.point} is not in ${chunkIterator.name}")
          errors += 1
        }
      }

      for (referenceMapcodeRec <- reference.mapcodes) {
        val found = results.exists { result =>
            referenceMapcodeRec match {
              case SimpleMapcode(code, Some(terr)) if terr == result.territory =>
                (code.lastIndexOf('-') > 4 && code == result.mapcodePrecision2) || code == result.mapcode
              case _ => false
            }
        }
        if (!found) {
          println(s"checkFile: Mapcode '${referenceMapcodeRec.territory} ${referenceMapcodeRec.mapcode}' " +
            s"at ${reference.point} is not produced by the decoder!")
          errors += 1
        }
      }

      for (mapcodeRec <- reference.mapcodes) {
        try {
          val result = MapcodeCodec.decode(mapcodeRec.mapcode, mapcodeRec.territory.get)
          val distanceMeters = reference.point.distanceInMeters(result)
          maxDelta = Math.max(maxDelta, distanceMeters)
          if (distanceMeters > METERS_DELTA) {
            println(s"Mapcode ${mapcodeRec.territory} ${mapcodeRec.mapcode} was generated " +
              s"for point ${reference.point}, but decodes to point $result which is $distanceMeters " +
              s"meters from the original point.")
            errors += 1
          }
        }
        catch {
          case unknownMapcodeException: UnknownMapcodeException =>
            println(s"Mapcode ${mapcodeRec.territory} ${mapcodeRec.mapcode} was generated for " +
              s"point ${reference.point}, but cannot be decoded.")
            errors += 1
        }
      }
      count += 1
    }
    println(s"checkFile: Maximum delta for this test set ($baseFileName) = $maxDelta")
    errors should be(0)
  }
}


object ReferenceFileTest extends Matchers {
  val RANDOM_REFERENCE_FILE_1: String = "/random_1k.txt"
  val RANDOM_REFERENCE_FILE_2: String = "/random_10k.txt"
  val RANDOM_REFERENCE_FILE_3: String = "/random_100k.txt"
  val RANDOM_REFERENCE_FILE_1_HP: String = "/random_hp_1k.txt"
  val RANDOM_REFERENCE_FILE_2_HP: String = "/random_hp_10k.txt"
  val RANDOM_REFERENCE_FILE_3_HP: String = "/random_hp_100k.txt"
  val GRID_REFERENCE_FILE_1: String = "/grid_1k.txt"
  val GRID_REFERENCE_FILE_2: String = "/grid_10k.txt"
  val GRID_REFERENCE_FILE_3: String = "/grid_100k.txt"
  val GRID_REFERENCE_FILE_1_HP: String = "/grid_hp_1k.txt"
  val GRID_REFERENCE_FILE_2_HP: String = "/grid_hp_10k.txt"
  val GRID_REFERENCE_FILE_3_HP: String = "/grid_hp_100k.txt"
  val BOUNDARIES_REFERENCE_FILE: String = "/boundaries.txt"
  val BOUNDARIES_REFERENCE_FILE_HP: String = "/boundaries_hp.txt"

  private def mkSuffixStream: Stream[Char] = Stream.tabulate(10)(x => ('a' + x).toChar)

  case class SimpleMapcode(mapcode: String, territory: Option[Territory.Territory])

  case class Reference(point: Point, mapcodes: Seq[SimpleMapcode])

  case class ChunkIterator(name: String) extends Iterator[Reference] {

    val (sources, iterator) = {
      val suffixes = mkSuffixStream
      val names = suffixes.map(sfx => name + "." + sfx)
      val streams = names.map(name => getClass.getResourceAsStream(name)).takeWhile(_ != null)
      val sources = streams.map(io.Source.fromInputStream)
      val iterator = sources.flatMap(source => source.getLines()).filterNot(_.isEmpty).toIterator
      (sources, iterator)
    }

    override def hasNext: Boolean = iterator.hasNext

    def next(): Reference = {
      val firstLine = iterator.next()
      val args = firstLine.split(" ")
      assert(args.length == 3 || args.length == 6, s"Expecting 3 or 6 elems, not ${args.length} in: [$firstLine]")

      val (count, latDeg, lonDeg) = (args(0).toInt, args(1).toDouble, args(2).toDouble)

      checkRange(s"Expecting between 1 and 21 mapcodes; got $count", count, 1, 21)
      checkRange(s"Latitude must be in [-90, 90]: $latDeg", latDeg, -90, 90)
      checkRange(s"Longitude must be in [-180, 180]: $lonDeg", lonDeg, -180, 180)

      val point = Point.fromMicroDeg(Point.degToMicroDeg(latDeg), Point.degToMicroDeg(lonDeg))
      val mapcodeRecs = for (i <- 0 until count) yield {
        val Array(territoryStr, mapcode) = iterator.next().split(" ")
        SimpleMapcode(mapcode, Territory.fromString(territoryStr))
      }
      Reference(point, mapcodeRecs)
    }

  }
}

