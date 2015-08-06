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

import com.mapcode.UnknownMapcodeException
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSuite, Matchers}

class DecoderTest extends FunSuite with Matchers with GeneratorDrivenPropertyChecks {

  test("decodeMapcodeWithTerritory") {
    val point = MapcodeCodec.decode("49.4V", Territory.NLD)
    52.376514 shouldBe point.latDeg
    4.908542 shouldBe (point.lonDeg +- 0.00001)
  }

  test("decodeUpperLowercaseMapcode") {
    val point1 = MapcodeCodec.decode("XXXXX.1234")
    59.596312 shouldBe point1.latDeg
    155.931892 shouldBe point1.lonDeg

    val point2 = MapcodeCodec.decode("Xxxxx.1234")
    59.596312 shouldBe point2.latDeg
    155.931892 shouldBe point2.lonDeg

    val point3 = MapcodeCodec.decode("xxxxx.1234")
    59.596312 shouldBe point3.latDeg
    155.931892 shouldBe point3.lonDeg
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
    an[IllegalArgumentException] should be thrownBy MapcodeCodec.decode(null, Territory.NLD)
  }

  test("nullArgument2") {
    an[NullPointerException] should be thrownBy MapcodeCodec.decode("494.V494V", null)
  }

  test("nullArgument3") {
    an[IllegalArgumentException] should be thrownBy MapcodeCodec.decode(null)
  }
}
