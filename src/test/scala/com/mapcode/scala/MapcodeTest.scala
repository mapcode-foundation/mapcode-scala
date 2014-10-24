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


class MapcodeTest extends FunSuite with Matchers {
  test("checkValidMapcodeFormats") {
    Mapcode.isValidMapcodeFormat("A1.B1") should be(true)
    Mapcode.isValidMapcodeFormat("a1.B1") should be(true)
    Mapcode.isValidMapcodeFormat("00.01") should be(true)
    Mapcode.isValidMapcodeFormat("AAA.01") should be(true)
    Mapcode.isValidMapcodeFormat("AAA.BBB") should be(true)
    Mapcode.isValidMapcodeFormat("AAAA.BBB") should be(true)
    Mapcode.isValidMapcodeFormat("AAAA.BBBB") should be(true)
    Mapcode.isValidMapcodeFormat("AAAAA.BBBB") should be(true)
    Mapcode.isValidMapcodeFormat("AAAAA.BBBBB") should be(true)
    Mapcode.isValidMapcodeFormat("AA.AA-0") should be(true)
    Mapcode.isValidMapcodeFormat("AA.AA-01") should be(true)
    Mapcode.isValidMapcodeFormat("AA.AA-A") should be(true)
    Mapcode.isValidMapcodeFormat("AA.AA-AA") should be(true)
    Mapcode.isValidMapcodeFormat("AA.AA-Y") should be(true)
    Mapcode.isValidMapcodeFormat("AA.AA-1Y") should be(true)
  }

  test("checkInvalidMapcodeFormats") {
    Mapcode.isValidMapcodeFormat("A") should be(false)
    Mapcode.isValidMapcodeFormat("AB") should be(false)
    Mapcode.isValidMapcodeFormat("AB.") should be(false)
    Mapcode.isValidMapcodeFormat(".A") should be(false)
    Mapcode.isValidMapcodeFormat(".AB") should be(false)
    Mapcode.isValidMapcodeFormat("A.B") should be(false)
    Mapcode.isValidMapcodeFormat("a.B") should be(false)
    Mapcode.isValidMapcodeFormat("0.1") should be(false)
    Mapcode.isValidMapcodeFormat("0.1") should be(false)
    Mapcode.isValidMapcodeFormat("00.1") should be(false)
    Mapcode.isValidMapcodeFormat("0.01") should be(false)
    Mapcode.isValidMapcodeFormat("00.01.") should be(false)
    Mapcode.isValidMapcodeFormat("00.01.0") should be(false)
    Mapcode.isValidMapcodeFormat("00.01.00") should be(false)
    Mapcode.isValidMapcodeFormat("00.01-") should be(false)
    Mapcode.isValidMapcodeFormat("00.01-") should be(false)
    Mapcode.isValidMapcodeFormat("AAAAAA.BBBBB") should be(false)
    Mapcode.isValidMapcodeFormat("AAAAA.BBBBBB") should be(false)
    Mapcode.isValidMapcodeFormat("AA.AA-012") should be(false)
    Mapcode.isValidMapcodeFormat("AA.AA-Z") should be(false)
    Mapcode.isValidMapcodeFormat("AA.AA-1Z") should be(false)
    Mapcode.isValidMapcodeFormat("A.AAA") should be(false)
    Mapcode.isValidMapcodeFormat("AAA.A") should be(false)
    Mapcode.isValidMapcodeFormat("A.AAA-1") should be(false)
    Mapcode.isValidMapcodeFormat("AAA.A-1") should be(false)
    Mapcode.isValidMapcodeFormat("A.AAA-12") should be(false)
    Mapcode.isValidMapcodeFormat("AAA.A-12") should be(false)
  }

  test("checkMapcodeFormatType") {
    Mapcode.MapcodeFormatType.MAPCODE_TYPE_INVALID should be(Mapcode.getMapcodeFormatType("ABC"))
    Mapcode.MapcodeFormatType.MAPCODE_TYPE_PRECISION_0 should be(Mapcode.getMapcodeFormatType("AA.BB"))
    Mapcode.MapcodeFormatType.MAPCODE_TYPE_PRECISION_1 should be(Mapcode.getMapcodeFormatType("AA.BB-1"))
    Mapcode.MapcodeFormatType.MAPCODE_TYPE_PRECISION_2 should be(Mapcode.getMapcodeFormatType("AA.BB-12"))
  }

  test("checkConvertToAscii") {
    Mapcode.convertToAscii("\u30c1\u30ca.8\u30c1") should be("KM.8K")
    Mapcode.convertToAscii("\u0397\u03a0.\u03982-\u0411") should be("HJ.Q2-Z")
  }

  test("invalid map code") {
    an[IllegalArgumentException] should be thrownBy Mapcode("", Territory.AAA)
  }
}
