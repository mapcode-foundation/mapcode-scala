/*
 * Copyright (C) 2014-2015 Stichting Mapcode Foundation (http://www.mapcode.com)
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

import scala.util.Try

class ReferenceFileTest extends FunSuite with Matchers {

  import com.mapcode.scala.ReferenceFileTest._

  test("allFiles") {
    AllFiles.par.foreach(checkFile)
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
  val RandomFiles = Seq("/random_1k.txt", "/random_10k.txt", "/random_100k.txt")
  val RandomHpFiles = Seq("/random_hp_1k.txt", "/random_hp_10k.txt", "/random_hp_100k.txt")
  val GridFiles = Seq("/grid_1k.txt", "/grid_10k.txt", "/grid_100k.txt")
  val GridHpFiles = Seq("/grid_hp_1k.txt", "/grid_hp_10k.txt", "/grid_hp_100k.txt")
  val BoundaryFiles = Seq("/boundaries.txt")
  val BoundaryHpFiles = Seq("/boundaries_hp.txt")
  val AllFiles = Seq(RandomFiles, RandomHpFiles, GridFiles, GridHpFiles, BoundaryFiles, BoundaryHpFiles).flatten

  // make sure all these files exist!
  AllFiles.map(name => (name, getClass.getResourceAsStream(name + ".a"))).map { ni =>
    require(ni._2 != null, s"${ni._1} not found"); ni._2
  }.map(i => Try(i.close()))

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
