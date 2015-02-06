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
    Mapcode.isValidMapcodeFormat("A1.B1") shouldBe true
    Mapcode.isValidMapcodeFormat("a1.B1") shouldBe true
    Mapcode.isValidMapcodeFormat("00.01") shouldBe true
    Mapcode.isValidMapcodeFormat("AAA.01") shouldBe true
    Mapcode.isValidMapcodeFormat("AAA.BBB") shouldBe true
    Mapcode.isValidMapcodeFormat("AAAA.BBB") shouldBe true
    Mapcode.isValidMapcodeFormat("AAAA.BBBB") shouldBe true
    Mapcode.isValidMapcodeFormat("AAAAA.BBBB") shouldBe true
    Mapcode.isValidMapcodeFormat("AAAAA.BBBBB") shouldBe true
    Mapcode.isValidMapcodeFormat("AA.AA-0") shouldBe true
    Mapcode.isValidMapcodeFormat("AA.AA-01") shouldBe true
    Mapcode.isValidMapcodeFormat("AA.AA-A") shouldBe true
    Mapcode.isValidMapcodeFormat("AA.AA-AA") shouldBe true
    Mapcode.isValidMapcodeFormat("AA.AA-Y") shouldBe true
    Mapcode.isValidMapcodeFormat("AA.AA-1Y") shouldBe true
  }

  test("checkInvalidMapcodeFormats") {
    Mapcode.isValidMapcodeFormat("A") shouldBe false
    Mapcode.isValidMapcodeFormat("AB") shouldBe false
    Mapcode.isValidMapcodeFormat("AB.") shouldBe false
    Mapcode.isValidMapcodeFormat(".A") shouldBe false
    Mapcode.isValidMapcodeFormat(".AB") shouldBe false
    Mapcode.isValidMapcodeFormat("A.B") shouldBe false
    Mapcode.isValidMapcodeFormat("a.B") shouldBe false
    Mapcode.isValidMapcodeFormat("0.1") shouldBe false
    Mapcode.isValidMapcodeFormat("0.1") shouldBe false
    Mapcode.isValidMapcodeFormat("00.1") shouldBe false
    Mapcode.isValidMapcodeFormat("0.01") shouldBe false
    Mapcode.isValidMapcodeFormat("00.01.") shouldBe false
    Mapcode.isValidMapcodeFormat("00.01.0") shouldBe false
    Mapcode.isValidMapcodeFormat("00.01.00") shouldBe false
    Mapcode.isValidMapcodeFormat("00.01-") shouldBe false
    Mapcode.isValidMapcodeFormat("00.01-") shouldBe false
    Mapcode.isValidMapcodeFormat("AAAAAA.BBBBB") shouldBe false
    Mapcode.isValidMapcodeFormat("AAAAA.BBBBBB") shouldBe false
    Mapcode.isValidMapcodeFormat("AA.AA-012") shouldBe false
    Mapcode.isValidMapcodeFormat("AA.AA-Z") shouldBe false
    Mapcode.isValidMapcodeFormat("AA.AA-1Z") shouldBe false
    Mapcode.isValidMapcodeFormat("A.AAA") shouldBe false
    Mapcode.isValidMapcodeFormat("AAA.A") shouldBe false
    Mapcode.isValidMapcodeFormat("A.AAA-1") shouldBe false
    Mapcode.isValidMapcodeFormat("AAA.A-1") shouldBe false
    Mapcode.isValidMapcodeFormat("A.AAA-12") shouldBe false
    Mapcode.isValidMapcodeFormat("AAA.A-12") shouldBe false
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
