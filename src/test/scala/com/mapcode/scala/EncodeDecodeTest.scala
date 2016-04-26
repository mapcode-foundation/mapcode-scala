/*
 * Copyright (C) 2014-2016 Stichting Mapcode Foundation (http://www.mapcode.com)
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

import org.scalatest.{FunSuite, Matchers}

class EncodeDecodeTest extends FunSuite {
  test("encodeDecodeTestFixedSeed") {
    EncodeDecodeTest.doEncodeDecode(12345678)
  }

  test("encodeDecodeTestRandomSeed") {
    EncodeDecodeTest.doEncodeDecode(System.currentTimeMillis)
  }
}

// useful for profiling
object EncodeDecodeTestApp extends App {
  EncodeDecodeTest.doEncodeDecode(12345678)
}

object EncodeDecodeTest extends Matchers {
  private final val SampleSize = 1000
  private final val MaxErrorMeters = 10d

  def doEncodeDecode(seed: Long) {
    val randomGenerator = new java.util.Random(seed)
    var i: Int = 0
    while (i < SampleSize) {
      {
        var found = false
        val encode = Point.fromUniformlyDistributedRandomPoints(randomGenerator)
        for (territory <- Territory.values) {
          val (latDeg, lonDeg) = (encode.latDeg, encode.lonDeg)
          for (result <- MapcodeCodec.encode(latDeg, lonDeg, territory)) {
            found = true
            result.territory should be(territory)
            val decoded = MapcodeCodec.decode(result.code, territory)
            val distance = Point.distanceInMeters(encode, decoded)
            distance should be < MaxErrorMeters
            //log( f"""#$i/$SampleSize $encode/$territory) -> ${result.code} -> $decoded ($distance%1.2f m)""")
          }
        }
        found should be(right = true)
      }
      i += 1
    }
  }

}


