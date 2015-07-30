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


class MapcodeTest extends FunSuite with Matchers {
  test("checkValidMapcodeFormats") {
    Mapcode.isValidPrecisionFormat("A1.B1") shouldBe true
    Mapcode.isValidPrecisionFormat("a1.B1") shouldBe true
    Mapcode.isValidPrecisionFormat("00.01") shouldBe true
    Mapcode.isValidPrecisionFormat("AAA.01") shouldBe true
    Mapcode.isValidPrecisionFormat("AAA.BBB") shouldBe true
    Mapcode.isValidPrecisionFormat("AAAA.BBB") shouldBe true
    Mapcode.isValidPrecisionFormat("AAAA.BBBB") shouldBe true
    Mapcode.isValidPrecisionFormat("AAAAA.BBBB") shouldBe true
    Mapcode.isValidPrecisionFormat("AAAAA.BBBBB") shouldBe true
    Mapcode.isValidPrecisionFormat("AA.AA-0") shouldBe true
    Mapcode.isValidPrecisionFormat("AA.AA-01") shouldBe true
    Mapcode.isValidPrecisionFormat("AA.AA-A") shouldBe true
    Mapcode.isValidPrecisionFormat("AA.AA-AA") shouldBe true
    Mapcode.isValidPrecisionFormat("AA.AA-Y") shouldBe true
    Mapcode.isValidPrecisionFormat("AA.AA-1Y") shouldBe true
  }

  test("checkInvalidMapcodeFormats") {
    Mapcode.isValidPrecisionFormat("A") shouldBe false
    Mapcode.isValidPrecisionFormat("AB") shouldBe false
    Mapcode.isValidPrecisionFormat("AB.") shouldBe false
    Mapcode.isValidPrecisionFormat(".A") shouldBe false
    Mapcode.isValidPrecisionFormat(".AB") shouldBe false
    Mapcode.isValidPrecisionFormat("A.B") shouldBe false
    Mapcode.isValidPrecisionFormat("a.B") shouldBe false
    Mapcode.isValidPrecisionFormat("0.1") shouldBe false
    Mapcode.isValidPrecisionFormat("0.1") shouldBe false
    Mapcode.isValidPrecisionFormat("00.1") shouldBe false
    Mapcode.isValidPrecisionFormat("0.01") shouldBe false
    Mapcode.isValidPrecisionFormat("00.01.") shouldBe false
    Mapcode.isValidPrecisionFormat("00.01.0") shouldBe false
    Mapcode.isValidPrecisionFormat("00.01.00") shouldBe false
    Mapcode.isValidPrecisionFormat("00.01-") shouldBe false
    Mapcode.isValidPrecisionFormat("00.01-") shouldBe false
    Mapcode.isValidPrecisionFormat("AAAAAA.BBBBB") shouldBe false
    Mapcode.isValidPrecisionFormat("AAAAA.BBBBBB") shouldBe false
    Mapcode.isValidPrecisionFormat("AA.AA-012") shouldBe false
    Mapcode.isValidPrecisionFormat("AA.AA-Z") shouldBe false
    Mapcode.isValidPrecisionFormat("AA.AA-1Z") shouldBe false
    Mapcode.isValidPrecisionFormat("A.AAA") shouldBe false
    Mapcode.isValidPrecisionFormat("AAA.A") shouldBe false
    Mapcode.isValidPrecisionFormat("A.AAA-1") shouldBe false
    Mapcode.isValidPrecisionFormat("AAA.A-1") shouldBe false
    Mapcode.isValidPrecisionFormat("A.AAA-12") shouldBe false
    Mapcode.isValidPrecisionFormat("AAA.A-12") shouldBe false
  }

  test("checkMapcodeFormatType") {
    Mapcode.getMapcodeFormatType("ABC") should be(Mapcode.MapcodeFormat.Invalid)
    Mapcode.getMapcodeFormatType("AA.BB") should be(Mapcode.MapcodeFormat.Precision0)
    Mapcode.getMapcodeFormatType("AA.BB-1") should be(Mapcode.MapcodeFormat.Precision1)
    Mapcode.getMapcodeFormatType("AA.BB-12") should be(Mapcode.MapcodeFormat.Precision2)
  }

  test("checkConvertToAscii") {
    Mapcode.convertToAscii("\u30c1\u30ca.8\u30c1") should be("KM.8K")
    Mapcode.convertToAscii("\u0397\u03a0.\u03982-\u0411") should be("HJ.Q2-Z")
  }

  test("invalid map code") {
    an[IllegalArgumentException] should be thrownBy Mapcode("", Territory.AAA)
  }
}
