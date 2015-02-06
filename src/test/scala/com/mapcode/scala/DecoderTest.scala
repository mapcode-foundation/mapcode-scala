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

import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSuite, Matchers}

class DecoderTest extends FunSuite with Matchers with GeneratorDrivenPropertyChecks {
  test("decodeTomTomOffice1") {
    val point: Point = MapcodeCodec.decode("49.4V", Territory.NLD)
    point.latMicroDeg should be(52376514)
    point.lonMicroDeg should be(4908542)
  }

  test("decodeTomTomOffice2") {
    val point: Point = MapcodeCodec.decode("NLD 49.4V")
    point.latMicroDeg should be(52376514)
    point.lonMicroDeg should be(4908542)
  }

  test("highPrecisionTomTomOffice1") {
    val point: Point = MapcodeCodec.decode("49.4V-K2", Territory.NLD)
    point.latMicroDeg should be(52376512)
    point.lonMicroDeg should be(4908540)
  }

  test("highPrecisionTomTomOffice2") {
    val point: Point = MapcodeCodec.decode("NLD 49.4V-K2")
    point.latMicroDeg should be(52376512)
    point.lonMicroDeg should be(4908540)
  }

  test("highPrecisionUnicodeAthensAcropolis1") {
    val point: Point = MapcodeCodec.decode("\u0397\u03a0.\u03982-\u03a62", Territory.GRC)
    (point.latMicroDeg, point.lonMicroDeg) should be((37971844, 23726223))
  }

  test("highPrecisionUnicodeAthensAcropolis2") {
    val point: Point = MapcodeCodec.decode("GRC \u0397\u03a0.\u03982-\u03a62")
    (point.latMicroDeg, point.lonMicroDeg) should be((37971844, 23726223))
  }

  test("unicodeMapcodeAthensAcropolis1") {
    val point: Point = MapcodeCodec.decode("\u0397\u03a0.\u03982", Territory.GRC)
    (point.latMicroDeg, point.lonMicroDeg) should be((37971812, 23726247))
  }

  test("unicodeMapcodeAthensAcropolis2") {
    val point: Point = MapcodeCodec.decode("GRC \u0397\u03a0.\u03982")
    (point.latMicroDeg, point.lonMicroDeg) should be((37971812, 23726247))
  }

  test("unicodeMapcodeTokyoTower1") {
    val point: Point = MapcodeCodec.decode("\u30c1\u30ca.8\u30c1", Territory.JPN)
    (point.latMicroDeg, point.lonMicroDeg) should be((35658660, 139745394))
  }

  test("unicodeMapcodeTokyoTower2") {
    val point: Point = MapcodeCodec.decode("JPN \u30c1\u30ca.8\u30c1")
    (point.latMicroDeg, point.lonMicroDeg) should be((35658660, 139745394))
  }

  test("mapCodeWithZeroGroitzsch") {
    val point: Point = MapcodeCodec.decode("HMVM.3Q0", Territory.DEU)
    (point.latMicroDeg, point.lonMicroDeg) should be((51154852, 12278574))
  }

  test("invalidTerritory") {
    an[IllegalArgumentException] should be thrownBy MapcodeCodec.decode("NLD 49.4V", Territory.NLD)
  }

  test("invalidNoDot") {
    an[IllegalArgumentException] should be thrownBy MapcodeCodec.decode("494V", Territory.NLD)
  }

  test("invalidDotLocation1") {
    an[IllegalArgumentException] should be thrownBy MapcodeCodec.decode("4.94V", Territory.NLD)
  }

  test("invalidDotLocation2") {
    an[IllegalArgumentException] should be thrownBy MapcodeCodec.decode("494.V", Territory.NLD)
  }

  test("invalidDotLocation3") {
    an[IllegalArgumentException] should be thrownBy MapcodeCodec.decode("494V49.4V", Territory.NLD)
  }

  test("invalidMapcode1") {
    an[UnknownMapcodeException] should be thrownBy MapcodeCodec.decode("494.V494V", Territory.NLD)
  }

  test("invalidHighPrecisionCharacter") {
    an[IllegalArgumentException] should be thrownBy MapcodeCodec.decode("49.4V-Z", Territory.NLD)
  }

  test("invalidHighPrecisionCharacter2") {
    an[IllegalArgumentException] should be thrownBy MapcodeCodec.decode("49.4V-HZ", Territory.NLD)
  }

  test("invalidHighPrecisionCharacter3") {
    an[IllegalArgumentException] should be thrownBy MapcodeCodec.decode("\u0397\u03a0.\u03982-\u0411", Territory.GRC)
  }

  test("invalidHighPrecisionCharacter4") {
    an[IllegalArgumentException] should be thrownBy MapcodeCodec.decode("\u0397\u03a0.\u03982-\u0411\u0411", Territory.GRC)
  }

  test("nullArgument1") {
    an[NullPointerException] should be thrownBy MapcodeCodec.decode(null, Territory.NLD)
  }

  test("nullArgument2") {
    an[NullPointerException] should be thrownBy MapcodeCodec.decode("494.V494V", null)
  }

  test("nullArgument3") {
    an[NullPointerException] should be thrownBy MapcodeCodec.decode(null)
  }

  test("decodeUTF16 should be able to correctly process any string input") {
    forAll {
      (str: String) =>
        val converted = Decoder.decodeUTF16(str)
        converted.size should be(str.size)
    }
  }
}
