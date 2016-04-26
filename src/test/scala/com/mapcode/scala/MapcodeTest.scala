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
    Mapcode.isValidMapcodeFormat("AA.AA-0") shouldBe true
    Mapcode.isValidMapcodeFormat("AA.AA-01") shouldBe true
    Mapcode.isValidMapcodeFormat("AA.AA-A") shouldBe true
    Mapcode.isValidMapcodeFormat("AA.AA-AA") shouldBe true
    Mapcode.isValidMapcodeFormat("AA.AA-Y") shouldBe true
    Mapcode.isValidMapcodeFormat("AA.AA-1Y") shouldBe true
    Mapcode.isValidMapcodeFormat("AA.AA-012") shouldBe true
    Mapcode.isValidMapcodeFormat("AA.AA-0123") shouldBe true
    Mapcode.isValidMapcodeFormat("AA.AA-01234") shouldBe true
    Mapcode.isValidMapcodeFormat("AA.AA-012345") shouldBe true
    Mapcode.isValidMapcodeFormat("AA.AA-0123456") shouldBe true
    Mapcode.isValidMapcodeFormat("AA.AA-01234567") shouldBe true
  }

  test("checkInvalidMapcodeFormats") {
    Mapcode.isValidMapcodeFormat("AA.AA-012345678") shouldBe false
    Mapcode.isValidMapcodeFormat("AA.AA-0123456789") shouldBe false
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

    Mapcode.precisionFormat("AA.BB") should be(0)
    Mapcode.precisionFormat("AA.BB-1") should be(1)
    Mapcode.precisionFormat("AA.BB-12") should be(2)
    Mapcode.precisionFormat("AA.BB-123") should be(3)
    Mapcode.precisionFormat("AA.BB-1234") should be(4)
    Mapcode.precisionFormat("AA.BB-12345") should be(5)
    Mapcode.precisionFormat("AA.BB-123456") should be(6)
    Mapcode.precisionFormat("AA.BB-1234567") should be(7)
    Mapcode.precisionFormat("AA.BB-12345678") should be(8)
  }

  test("invalid map code") {
    an[IllegalArgumentException] should be thrownBy Mapcode("", Territory.AAA)
  }
}
