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

import org.scalatest.{FunSuite, Matchers}

class EncodeDecodeTest extends FunSuite {
  test("encodeDecodeTestFixedSeed") {
    EncodeDecodeTest.doEncodeDecode(12345678)
  }

  test("encodeDecodeTestRandomSeed") {
    EncodeDecodeTest.doEncodeDecode(System.currentTimeMillis)
  }
}

object EncodeDecodeTest extends Matchers {
  private final val SampleSize = 1000
  private final val LogEvery = 10
  private final val MaxErrorMeters = 10d

  private def doEncodeDecode(seed: Long) {
    val randomGenerator = new java.util.Random(seed)
    var i: Int = 0
    while (i < SampleSize) {
      {
        def log(msg: => String) = if ((i % LogEvery) == 0) println(msg)
        var found = false
        val encode = Point.fromUniformlyDistributedRandomPoints(randomGenerator)
        for (territory <- Territory.territories) {
          val (latDeg, lonDeg) = (encode.latDeg, encode.lonDeg)
          for (result <- MapcodeCodec.encode(latDeg, lonDeg, territory)) {
            found = true
            result.territory should equal(territory)
            val decoded = MapcodeCodec.decode(result.mapcode, territory)
            val distance = encode.distanceInMeters(decoded)
            distance should be < MaxErrorMeters
            log(f"""#$i/$SampleSize $encode/$territory) -> ${result.mapcode} -> $decoded ($distance%1.2f m)""")
          }
        }
        found should be(right = true)
      }
      i += 1
    }
  }

}


