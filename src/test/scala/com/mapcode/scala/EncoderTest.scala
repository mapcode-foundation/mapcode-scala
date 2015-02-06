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

import org.scalatest.{FunSuite, Matchers}

class EncoderTest extends FunSuite with Matchers {
  test("encodeWithTerritory") {
    val (lat, lon) = (52.376514, 4.908542)
    MapcodeCodec.encode(lat, lon, Territory.NLD).map(_.asInternationalISO) should be(
      Seq("NLD 49.4V", "NLD G9.VWG", "NLD DL6.H9L", "NLD P25Z.N3Z"))
  }

  test("encodeWithoutTerritory1") {
    val (lat, lon) = (52.376514, 4.908542)
    MapcodeCodec.encode(lat, lon).map(_.asInternationalISO) should be(
      Seq("NLD 49.4V", "NLD G9.VWG", "NLD DL6.H9L", "NLD P25Z.N3Z", "AAA VHXGB.1J9J"))
  }

  test("encodeWithoutTerritory2") {
    val (lat, lon) = (26.87016, 75.847)
    MapcodeCodec.encode(lat, lon).map(_.asInternationalISO) should be(
      Seq("IN-RJ XX.XX", "IN-RJ 6X.NHG", "IN-RJ KH9.FGV", "IN-RJ H8M.6FTF", "IN-RJ 8BZ9.D61B", "IN-MP H8M.6FTF",
        "IN-MP 8BZ9.D61B", "IND H8M.6FTF", "IND 8BZ9.D61B", "AAA PQ0PF.5M1H"))
  }

  test("encodeToInternational1") {
    val (lat, lon) = (52.376514, 4.908542)
    val mapcode = MapcodeCodec.encodeToInternational(lat, lon)
    mapcode.mapcode should be("VHXGB.1J9J")
    mapcode.asInternationalFullName should be("International VHXGB.1J9J")
    mapcode.asLocal should be("VHXGB.1J9J")
  }

  test("encodeToInternational2") {
    val (lat, lon) = (26.87016, 75.847)
    MapcodeCodec.encodeToInternational(lat, lon).mapcode should be("PQ0PF.5M1H")
  }

  test("encodeToShortest1") {
    val (lat, lon) = (52.376514, 4.908542)
    MapcodeCodec.encodeToShortest(lat, lon).asInternationalISO should be("NLD 49.4V")
  }

  test("encodeToShortest2") {
    val (lat, lon) = (26.87016, 75.847)
    MapcodeCodec.encodeToShortest(lat, lon).asInternationalISO should be("IN-RJ XX.XX")
  }

  test("encodeToShortest3") {
    val (lat, lon) = (52.376514, 4.908542)
    MapcodeCodec.encodeToShortest(lat, lon, Territory.NLD).asInternationalISO should be("NLD 49.4V")
  }

  test("encodeToShortest4") {
    val (lat, lon) = (26.87016, 75.847)
    an[UnknownMapcodeException] should be thrownBy MapcodeCodec.encodeToShortest(lat, lon, Territory.NLD)
  }

  test("legalArgument") {
    MapcodeCodec.encode(-90, 0)
    MapcodeCodec.encode(90, 0)
    MapcodeCodec.encode(0, -180)
    MapcodeCodec.encode(0, 180)
  }

  test("illegalArgument1") {
    an[IllegalArgumentException] should be thrownBy MapcodeCodec.encode(-91, 0)
    an[IllegalArgumentException] should be thrownBy MapcodeCodec.encodeToInternational(-91, 0)
    an[IllegalArgumentException] should be thrownBy MapcodeCodec.encodeToShortest(-91, 0)
  }

  test("illegalArgument2") {
    an[IllegalArgumentException] should be thrownBy MapcodeCodec.encode(91, 0)
    an[IllegalArgumentException] should be thrownBy MapcodeCodec.encodeToInternational(91, 0)
    an[IllegalArgumentException] should be thrownBy MapcodeCodec.encodeToShortest(91, 0)
  }

  test("illegalArgument3") {
    an[IllegalArgumentException] should be thrownBy MapcodeCodec.encode(0, -181)
    an[IllegalArgumentException] should be thrownBy MapcodeCodec.encodeToInternational(0, -181)
    an[IllegalArgumentException] should be thrownBy MapcodeCodec.encodeToShortest(0, -181)
  }

  test("illegalArgument4") {
    an[IllegalArgumentException] should be thrownBy MapcodeCodec.encode(0, 181)
    an[IllegalArgumentException] should be thrownBy MapcodeCodec.encodeToInternational(0, 181)
    an[IllegalArgumentException] should be thrownBy MapcodeCodec.encodeToShortest(0, 181)
  }

  test("illegalArgument5") {
    an[IllegalArgumentException] should be thrownBy MapcodeCodec.encode(0, 0, null)
    an[IllegalArgumentException] should be thrownBy MapcodeCodec.encodeToShortest(0, 181, null)
  }
}

