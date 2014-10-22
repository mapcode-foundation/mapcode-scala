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

/*
class MapcodeCodecTest extends FunSuite with Matchers {
  def test("decodeTomTomOffice1") {
    val point: Point = MapcodeCodec.decode("49.4V", Territory.NLD)
    assertEquals("decodeTomTomOffice latitude", 52376514, point.getLatMicroDeg)
    assertEquals("decodeTomTomOffice longitude", 4908542, point.getLonMicroDeg)
  }

  def test("decodeTomTomOffice2") {
    val point: Point = MapcodeCodec.decode("NLD 49.4V")
    assertEquals("decodeTomTomOffice latitude", 52376514, point.getLatMicroDeg)
    assertEquals("decodeTomTomOffice longitude", 4908542, point.getLonMicroDeg)
  }

  def test("highPrecisionTomTomOffice1") {
    val point: Point = MapcodeCodec.decode("49.4V-K2", Territory.NLD)
    assertEquals("decodeTomTomOffice hi-precision latitude", 52376512, point.getLatMicroDeg)
    assertEquals("decodeTomTomOffice hi-precision longitude", 4908540, point.getLonMicroDeg)
  }

  def test("highPrecisionTomTomOffice2") {
    val point: Point = MapcodeCodec.decode("NLD 49.4V-K2")
    assertEquals("decodeTomTomOffice hi-precision latitude", 52376512, point.getLatMicroDeg)
    assertEquals("decodeTomTomOffice hi-precision longitude", 4908540, point.getLonMicroDeg)
  }

  def test("highPrecisionUnicodeAthensAcropolis1") {
    val point: Point = MapcodeCodec.decode("\u0397\u03a0.\u03982-\u03a62", Territory.GRC)
    assertEquals("decodeUnicode latitude", 37971844, point.getLatMicroDeg)
    assertEquals("decodeUnicode longitude", 23726223, point.getLonMicroDeg)
  }

  def test("highPrecisionUnicodeAthensAcropolis2") {
    val point: Point = MapcodeCodec.decode("GRC \u0397\u03a0.\u03982-\u03a62")
    assertEquals("decodeUnicode latitude", 37971844, point.getLatMicroDeg)
    assertEquals("decodeUnicode longitude", 23726223, point.getLonMicroDeg)
  }

  def test("unicodeMapcodeAthensAcropolis1") {
    val point: Point = MapcodeCodec.decode("\u0397\u03a0.\u03982", Territory.GRC)
    assertEquals("decodeUnicode latitude", 37971812, point.getLatMicroDeg)
    assertEquals("decodeUnicode longitude", 23726247, point.getLonMicroDeg)
  }

  def test("unicodeMapcodeAthensAcropolis2") {
    val point: Point = MapcodeCodec.decode("GRC \u0397\u03a0.\u03982")
    assertEquals("decodeUnicode latitude", 37971812, point.getLatMicroDeg)
    assertEquals("decodeUnicode longitude", 23726247, point.getLonMicroDeg)
  }

  def test("unicodeMapcodeTokyoTower1") {
    val point: Point = MapcodeCodec.decode("\u30c1\u30ca.8\u30c1", Territory.JPN)
    assertEquals("decodeUnicode latitude", 35658660, point.getLatMicroDeg)
    assertEquals("decodeUnicode longitude", 139745394, point.getLonMicroDeg)
  }

  def test("unicodeMapcodeTokyoTower2") {
    val point: Point = MapcodeCodec.decode("JPN \u30c1\u30ca.8\u30c1")
    assertEquals("decodeUnicode latitude", 35658660, point.getLatMicroDeg)
    assertEquals("decodeUnicode longitude", 139745394, point.getLonMicroDeg)
  }

  def test("mapCodeWithZeroGroitzsch") {
    val point: Point = MapcodeCodec.decode("HMVM.3Q0", Territory.DEU)
    assertEquals("decodeUnicode latitude", 51154852, point.getLatMicroDeg)
    assertEquals("decodeUnicode longitude", 12278574, point.getLonMicroDeg)
  }

  def test("invalidTerritory") {
    an[IllegalArgumentException] should be thrownBy MapcodeCodec.decode("NLD 49.4V", Territory.NLD)
  }

  def test("invalidNoDot") {
    an[IllegalArgumentException] should be thrownBy MapcodeCodec.decode("494V", Territory.NLD)
  }

  def test("invalidDotLocation1") {
    an[IllegalArgumentException] should be thrownBy MapcodeCodec.decode("4.94V", Territory.NLD)
  }

  def test("invalidDotLocation2") {
    an[IllegalArgumentException] should be thrownBy MapcodeCodec.decode("494.V", Territory.NLD)
  }

  def test("invalidDotLocation3") {
    an[IllegalArgumentException] should be thrownBy MapcodeCodec.decode("494V49.4V", Territory.NLD)
  }

  def test("invalidMapcode1") {
    an[IllegalArgumentException] should be thrownBy MapcodeCodec.decode("494.V494V", Territory.NLD)
  }

  def test("invalidHighPrecisionCharacter") {
    an[IllegalArgumentException] should be thrownBy MapcodeCodec.decode("49.4V-Z", Territory.NLD)
  }

  def test("invalidHighPrecisionCharacter2") {
    an[IllegalArgumentException] should be thrownBy MapcodeCodec.decode("49.4V-HZ", Territory.NLD)
  }

  def test("invalidHighPrecisionCharacter3") {
    an[IllegalArgumentException] should be thrownBy MapcodeCodec.decode("\u0397\u03a0.\u03982-\u0411", Territory.GRC)
  }

  def test("invalidHighPrecisionCharacter4") {
    an[IllegalArgumentException] should be thrownBy MapcodeCodec.decode("\u0397\u03a0.\u03982-\u0411\u0411", Territory.GRC)
  }

  def test("illegalArgument1") {
    an[IllegalArgumentException] should be thrownBy MapcodeCodec.decode(null, Territory.NLD)
  }

  def test("illegalArgument2") {
    an[IllegalArgumentException] should be thrownBy MapcodeCodec.decode("494.V494V", null)
  }

  def test("illegalArgument3") {
    an[IllegalArgumentException] should be thrownBy MapcodeCodec.decode(null)
  }
}
*/
