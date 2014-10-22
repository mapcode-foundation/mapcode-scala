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

case class UnknownMapcodeException(message: String) extends Exception(message)


object Decoder {
  private final val CCODE_EARTH: Int = 540
  private final val decode_chars: Array[Int] = Array(-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, -1, -1, -1, -1, -1, -1, -1, -2, 10, 11, 12, -3, 13, 14, 15, 1, 16,
    17, 18, 19, 20, 0, 21, 22, 23, 24, 25, -4, 26, 27, 28, 29, 30, -1, -1, -1, -1, -1, -1, -2, 10, 11, 12, -3, 13, 14,
    15, 1, 16, 17, 18, 19, 20, 0, 21, 22, 23, 24, 25, -4, 26, 27, 28, 29, 30, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
    -1, -1, -1, -1, -1, -1, -1)
  private final val UNICODE2ASCII = Array(Unicode2Ascii(0x0041, 0x005a, "ABCDEFGHIJKLMNOPQRSTUVWXYZ"),
    Unicode2Ascii(0x0041, 0x005a, "ABCDEFGHIJKLMNOPQRSTUVWXYZ"), // Roman
    Unicode2Ascii(0x0391, 0x03a9, "ABGDFZHQIKLMNCOJP?STYVXRW"), // Greek
    Unicode2Ascii(0x0410, 0x042f,
      "AZBGDEFNI?KLMHOJPCTYQXSVW????U?R"), // Cyrillic
    Unicode2Ascii(0x05d0, 0x05ea, "ABCDFIGHJKLMNPQ?ROSETUVWXYZ"), // Hebrew
    Unicode2Ascii(0x0905, 0x0939,
      "A?????????E?????B?CD?F?G??HJZ?KL?MNP?QU?RS?T?V??W??XY"), // Hindi
    Unicode2Ascii(0x0d07, 0x0d39,
      "I?U?E??????A??BCD??F?G??HOJ??KLMNP?????Q?RST?VWX?YZ"), // Malai
    Unicode2Ascii(0x10a0, 0x10bf,
      "AB?CE?D?UF?GHOJ?KLMINPQRSTVW?XYZ"), // Georgisch
    Unicode2Ascii(
      0x30a2,
      0x30f2,
      "A?I?O?U?EB?C?D?F?G?H???J???????K??????L?M?N?????P??Q??R??S?????TV?????WX???Y????Z"), // Katakana
    Unicode2Ascii(0x0e01, 0x0e32,
      "BC?D??FGHJ??O???K??L?MNP?Q?R????S?T?V?W????UXYZAIE"), // Thai
    Unicode2Ascii(0x0e81, 0x0ec6,
      "BC?D??FG?H??J??????K??L?MN?P?Q??RST???V??WX?Y?ZA????????????U?????EI?O"), // Lao
    Unicode2Ascii(0x0532, 0x0556,
      "BCDE??FGHI?J?KLM?N?U?PQ?R??STVWXYZ?OA"), // Armenian
    Unicode2Ascii(0x0985, 0x09b9,
      "A??????B??E???U?CDF?GH??J??KLMNPQR?S?T?VW?X??Y??????Z"), // Bengali
    Unicode2Ascii(0x0a05, 0x0a39,
      "A?????????E?????B?CD?F?G??HJZ?KL?MNP?QU?RS?T?V??W??XY"), // Gurmukhi
    Unicode2Ascii(0x0f40, 0x0f66,
      "BCD?FGHJ??K?L?MN?P?QR?S?A?????TV?WXYEUZ"), // Tibetan

    Unicode2Ascii(0x0966, 0x096f, ""), // Hindi
    Unicode2Ascii(0x0d66, 0x0d6f, ""), // Malai
    Unicode2Ascii(0x0e50, 0x0e59, ""), // Thai
    Unicode2Ascii(0x09e6, 0x09ef, ""), // Bengali
    Unicode2Ascii(0x0a66, 0x0a6f, ""), // Gurmukhi
    Unicode2Ascii(0x0f20, 0x0f29, ""), // Tibetan

    // lowercase variants: greek, georgisch
    Unicode2Ascii(0x03B1, 0x03c9, "ABGDFZHQIKLMNCOJP?STYVXRW"), // Greek
    // lowercase
    Unicode2Ascii(0x10d0, 0x10ef,
      "AB?CE?D?UF?GHOJ?KLMINPQRSTVW?XYZ"), // Georgisch lowercase
    Unicode2Ascii(0x0562, 0x0586,
      "BCDE??FGHI?J?KLM?N?U?PQ?R??STVWXYZ?OA"), // Armenian
    // lowercase
    Unicode2Ascii(0, 0, null))
  private val group1 = Set(Territory.USA, Territory.CAN, Territory.AUS, Territory.BRA, Territory.CHN, Territory.RUS)
  private val group2 = Set(Territory.IND, Territory.MEX)

  private[scala] def decode(argMapcode: String, argTerritory: Territory.Territory): Point = {
    var mapcode = argMapcode
    var territory = argTerritory
    var result = Point.undefined
    var extrapostfix = ""
    val minpos = mapcode.indexOf('-')
    if (minpos > 0) {
      extrapostfix = decodeUTF16(mapcode.substring(minpos + 1).trim)
      if (extrapostfix.contains("Z")) {
        throw new UnknownMapcodeException("Invalid character Z")
      }
      mapcode = mapcode.substring(0, minpos)
    }
    mapcode = aeuUnpack(mapcode).trim
    if (mapcode.isEmpty) {
      result
    } else {
      val incodexlen: Int = mapcode.length - 1
      if (incodexlen >= 9) {
        territory = Territory.AAA
      }
      else {
        val parentTerritory = territory.parentTerritory
        parentTerritory.foreach { parent =>
          if (incodexlen >= 8 && parentTerritory.exists(group1) || incodexlen >= 7 && parentTerritory.exists(group2)) {
            territory = parent
          }
        }
      }
      val ccode = territory.territoryCode
      val from = DataAccess.dataFirstRecord(ccode)
      if (DataAccess.dataFlags(from) == 0) {
        Point.undefined
      } else {
        val upto = DataAccess.dataLastRecord(ccode)
        val incodexhi = mapcode.indexOf('.')
        var i: Int = from
        var done = false
        while (!done && i <= upto) {
          var mapcoderData = Data(i)
          mapcoderData match {
            case Data(_, _, _, `incodexhi`, `incodexlen`, false, _, _, 0, _, _, Some(rect)) =>
              result = decodeGrid(mapcode, rect.minX, rect.minY, rect.maxX, rect.maxY, i, extrapostfix)
              // RESTRICTUSELESS
              if (mapcoderData.useless && result.isDefined) {
                var fitssomewhere = false
                var i = 0
                var j = upto - 1
                while (!fitssomewhere && j >= from) {
                  mapcoderData = Data.apply(j)
                  if (!mapcoderData.useless) {
                    val Some(rect) = mapcoderData.mapcoderRect
                    val xdiv8 = Common.xDivider(rect.minY, rect.maxY) / 4
                    if (rect.extendBounds(xdiv8, 60).containsPoint(result)) {
                      fitssomewhere = true
                    }
                  }
                  j -= 1
                }
                if (!fitssomewhere) result = Point.undefined
              }
              done = true
            case Data(_, _, _, codexHi, codexLen, _, _, _, 4, Some(pipeLetter), _, Some(rect))
              if codexHi == incodexhi - 1 && codexLen == incodexlen - 1 && pipeLetter(0) == mapcode(0) =>
              result = decodeGrid(mapcode.substring(1), rect.minX, rect.minY, rect.maxX, rect.maxY, i, extrapostfix)
              done = true
            case Data(_, codex, _, codexHi, codexLen, nameless, _, _, _, _, _, _)
              if nameless && (codex == 21 && incodexlen == 4 && incodexhi == 2 ||
                codex == 22 && incodexlen == 5 && incodexhi == 3 ||
                codex == 13 && incodexlen == 5 && incodexhi == 2) =>
              result = decodeNameless(mapcode, i, extrapostfix, mapcoderData)
              done = true
            case Data(_, _, _, _, codexLen, _, _, _, pipeType, _, _, _)
              if pipeType > 4 && incodexlen == incodexhi + 3 && codexLen + 1 == incodexlen =>
              result = decodeStarpipe(mapcode, i, extrapostfix, mapcoderData)
              done = true
            case _ =>
          }
          i += 1
        }
        if (result.isDefined) {
          if (result.lonMicroDeg > 180000000) {
            result = Point.fromMicroDeg(result.latMicroDeg, result.lonMicroDeg - 360000000)
          }
          else if (result.lonMicroDeg < -180000000) {
            result = Point.fromMicroDeg(result.latMicroDeg, result.lonMicroDeg + 360000000)
          }
          if (ccode != CCODE_EARTH) {
            val Some(mapcoderRect) = SubArea.getArea(upto)
            val xdiv8 = Common.xDivider(mapcoderRect.minY, mapcoderRect.maxY) / 4
            if (!mapcoderRect.extendBounds(xdiv8, 60).containsPoint(result)) {
              result = Point.undefined
            }
          }
        }
        result.normalize
      }
    }
  }

  private def decodeGrid(str: String, minx: Int, miny: Int, maxx: Int, maxy: Int, m: Int, extrapostfix: String): Point = {
    var result: String = str
    var relx: Int = 0
    var rely: Int = 0
    val codexlen: Int = result.length - 1
    var dc: Int = result.indexOf('.')
    if (dc == 1 && codexlen == 5) {
      dc += 1
      result = result.substring(0, 1) + result.charAt(2) + '.' + result.substring(3)
    }
    val codexlow: Int = codexlen - dc
    val codex: Int = 10 * dc + codexlow
    var divx: Int = 0
    var divy: Int = 0
    divy = DataAccess.smartDiv(m)
    if (divy == 1) {
      divx = Common.xSide(dc)
      divy = Common.ySide(dc)
    }
    else {
      divx = Common.nc(dc) / divy
    }
    if (dc == 4 && divx == Common.xSide(4) && divy == Common.ySide(4)) {
      result = result.substring(0, 1) + result.charAt(2) + result.charAt(1) + result.substring(3)
    }
    var v: Int = fastDecode(result)
    if (divx != divy && codex > 24) {
      val d: Point = decode6(v, divx, divy)
      relx = d.lonMicroDeg
      rely = d.latMicroDeg
    }
    else {
      relx = v / divy
      rely = v % divy
      rely = divy - 1 - rely
    }
    val ygridsize: Int = (maxy - miny + divy - 1) / divy
    val xgridsize: Int = (maxx - minx + divx - 1) / divx
    rely = miny + rely * ygridsize
    relx = minx + relx * xgridsize
    val dividery: Int = (ygridsize + Common.ySide(codexlow) - 1) / Common.ySide(codexlow)
    val dividerx: Int = (xgridsize + Common.xSide(codexlow) - 1) / Common.xSide(codexlow)
    var rest: String = result.substring(dc + 1)
    var difx: Int = 0
    var dify: Int = 0
    val nrchars: Int = rest.length
    if (nrchars == 3) {
      val d: Point = decodeTriple(rest)
      difx = d.lonMicroDeg
      dify = d.latMicroDeg
    }
    else {
      if (nrchars == 4) {
        rest = String.valueOf(rest.charAt(0)) + rest.charAt(2) + rest.charAt(1) + rest.charAt(3)
      }
      v = fastDecode(rest)
      difx = v / Common.ySide(nrchars)
      dify = v % Common.ySide(nrchars)
    }
    dify = Common.ySide(nrchars) - 1 - dify
    val cornery: Int = rely + dify * dividery
    val cornerx: Int = relx + difx * dividerx
    add2res(cornery, cornerx, dividerx << 2, dividery, 1, extrapostfix)
  }

  private def decodeNameless(str: String, firstrec: Int, extrapostfix: String, argMapcoderData: Data): Point = {
    var mapcoderData = argMapcoderData
    var result: String = str
    if (mapcoderData.codex == 22) {
      result = result.substring(0, 3) + result.substring(4)
    }
    else {
      result = result.substring(0, 2) + result.substring(3)
    }
    var a: Int = Common.countCityCoordinatesForCountry(mapcoderData.codex, firstrec, firstrec)
    if (a < 2) {
      a = 1
    }
    val p: Int = 31 / a
    val r: Int = 31 % a
    var v: Int = 0
    var nrX: Int = 0
    var swapletters: Boolean = false
    if (mapcoderData.codex != 21 && a <= 31) {
      val offset: Int = decode_chars(result.charAt(0).asInstanceOf[Int])
      if (offset < r * (p + 1)) {
        nrX = offset / (p + 1)
      }
      else {
        swapletters = p == 1 && mapcoderData.codex == 22
        nrX = r + (offset - r * (p + 1)) / p
      }
    }
    else if (mapcoderData.codex != 21 && a < 62) {
      nrX = decode_chars(result.charAt(0).asInstanceOf[Int])
      if (nrX < (62 - a)) {
        swapletters = mapcoderData.codex == 22
      }
      else {
        nrX = nrX + nrX - 62 + a
      }
    }
    else {
      val basePower: Int = if (mapcoderData.codex == 21) 961 * 961 else 961 * 961 * 31
      var basePowerA: Int = basePower / a
      if (a == 62) {
        basePowerA += 1
      }
      else {
        basePowerA = 961 * (basePowerA / 961)
      }
      v = fastDecode(result)
      nrX = v / basePowerA
      v %= basePowerA
    }
    if (swapletters && !Data.isSpecialShape(firstrec + nrX)) {
      result = result.substring(0, 2) + result.charAt(3) + result.charAt(2) + result.charAt(4)
    }
    if (mapcoderData.codex != 21 && a <= 31) {
      v = fastDecode(result)
      if (nrX > 0) {
        v -= (nrX * p + (if (nrX < r) nrX else r)) * 961 * 961
      }
    }
    else if (mapcoderData.codex != 21 && a < 62) {
      v = fastDecode(result.substring(1))
      if (nrX >= (62 - a) && v >= (16 * 961 * 31)) {
        v -= 16 * 961 * 31
        nrX += 1
      }
    }
    if (nrX > a) {
      Point.undefined
    } else {
      mapcoderData = Data(firstrec + nrX)
      var side: Int = DataAccess.smartDiv(firstrec + nrX)
      var xSIDE: Int = side
      val Some(SubArea(Range(miny, maxy), Range(minx, _), _, _, _, _)) = mapcoderData.mapcoderRect
      var dx: Int = 0
      var dy: Int = 0
      if (mapcoderData.specialShape) {
        xSIDE *= side
        side = 1 + (maxy - miny) / 90
        xSIDE = xSIDE / side
        val d: Point = decode6(v, xSIDE, side)
        dx = d.lonMicroDeg
        dy = side - 1 - d.latMicroDeg
      }
      else {
        dy = v % side
        dx = v / side
      }
      if (dx >= xSIDE) {
        return Point.undefined
      }
      val dividerx4: Int = Common.xDivider(miny, maxy)
      val dividery: Int = 90
      val cornerx: Int = minx + (dx * dividerx4) / 4
      val cornery: Int = maxy - dy * dividery
      add2res(cornery, cornerx, dividerx4, dividery, -1, extrapostfix)
    }
  }

  private def decodeStarpipe(input: String, firstindex: Int, extrapostfix: String, argMapcoderData: Data): Point = {
    var mapcoderData = argMapcoderData
    var storageStart: Int = 0
    val thiscodexlen: Int = mapcoderData.codexLen
    var value: Int = fastDecode(input)
    value *= 961 * 31
    val triple: Point = decodeTriple(input.substring(input.length - 3))
    var i = firstindex
    var done = false
    var retval: Point = null
    while (!done) {
      if (Data.calcCodexLen(i) != thiscodexlen) {
        retval = Point.undefined
        done = true
      } else {
        if (i > firstindex) {
          mapcoderData = Data(i)
        }
        val Some(SubArea(Range(miny, maxy), Range(minx, maxx), _, _, _, _)) = mapcoderData.mapcoderRect
        var h: Int = (maxy - miny + 89) / 90
        val xdiv: Int = Common.xDivider(miny, maxy)
        var w: Int = ((maxx - minx) * 4 + xdiv - 1) / xdiv
        h = 176 * ((h + 176 - 1) / 176)
        w = 168 * ((w + 168 - 1) / 168)
        var product: Int = (w / 168) * (h / 176) * 961 * 31
        val goodRounder: Int = if (mapcoderData.codex >= 23) 961 * 961 * 31 else 961 * 961
        if (mapcoderData.pipeType == 8) {
          product = ((storageStart + product + goodRounder - 1) / goodRounder) * goodRounder - storageStart
        }
        if (value >= storageStart && value < storageStart + product) {
          val dividerx: Int = (maxx - minx + w - 1) / w
          val dividery: Int = (maxy - miny + h - 1) / h
          value -= storageStart
          value = value / (961 * 31)
          var vx: Int = value / (h / 176)
          vx = vx * 168 + triple.lonMicroDeg
          val vy: Int = (value % (h / 176)) * 176 + triple.latMicroDeg
          val cornery: Int = maxy - vy * dividery
          val cornerx: Int = minx + vx * dividerx
          retval = add2res(cornery, cornerx, dividerx << 2, dividery, -1, extrapostfix)
          done = true
        }
        storageStart += product
        i += 1
      }
    }
    retval
  }

  private def aeuUnpack(argStr: String): String = {
    var str: String = decodeUTF16(argStr)
    var voweled: Boolean = false
    val lastpos: Int = str.length - 1
    var dotpos: Int = str.indexOf('.')
    var result = ""
    if (dotpos >= 2 && lastpos >= dotpos + 2) {
      if (str.charAt(0) == 'A') {
        voweled = true
        str = str.substring(1)
        dotpos -= 1
      }
      else {
        var v: Int = str.charAt(lastpos - 1)
        if (v == 'A') {
          v = 0
        }
        else if (v == 'E') {
          v = 34
        }
        else if (v == 'U') {
          v = 68
        }
        else {
          v = -1
        }
        if (v >= 0) {
          val e: Char = str.charAt(lastpos)
          if (e == 'A') {
            v += 31
          }
          else if (e == 'E') {
            v += 32
          }
          else if (e == 'U') {
            v += 33
          }
          else {
            val ve: Int = decode_chars(str.charAt(lastpos).asInstanceOf[Int])
            if (ve < 0) {
              return ""
            }
            v += ve
          }
          if (v >= 100) {
            return ""
          }
          voweled = true
          str = str.substring(0, lastpos - 1) + Data.ENCODE_CHARS(v / 10) + Data.ENCODE_CHARS(v % 10)
        }
      }
      if (dotpos < 2 || dotpos > 5) {
        return ""
      }
      var v: Int = 0
      var done = false
      var result = str
      while (!done && v <= lastpos) {
        if (v != dotpos) {
          if (decode_chars(str.charAt(v).asInstanceOf[Int]) < 0) {
            result = ""
            done = true
          }
          else if (voweled && decode_chars(str.charAt(v).asInstanceOf[Int]) > 9) {
            result = ""
            done = true
          }
        }
        v += 1
      }
      result
    } else ""
  }

  /**
   * This method decodes a Unicode string to ASCII. Package private for access by other modules.
   *
   * @param str Unicode string.
   * @return ASCII string.
   */
  private[scala] def decodeUTF16(str: String): String = {
    val asciibuf: StringBuilder = new StringBuilder
    var index: Int = 0
    while (index < str.length) {
      if (str.charAt(index) == '.') {
        asciibuf.append(str.charAt(index))
      }
      else if (str.charAt(index) >= 1 && str.charAt(index) <= 'z') {
        asciibuf.append(str.charAt(index))
      }
      else {
        var found: Boolean = false
        var i: Int = 0
        while (!found && UNICODE2ASCII(i).min != 0) {
          if (str.charAt(index) >= UNICODE2ASCII(i).min && str.charAt(index) <= UNICODE2ASCII(i).max) {
            var convert: String = UNICODE2ASCII(i).convert
            if (convert == null) {
              convert = "0123456789"
            }
            asciibuf.append(convert.charAt(str.charAt(index).asInstanceOf[Int] - UNICODE2ASCII(i).min))
            found = true
          }
          i += 1
        }
        if (!found) {
          asciibuf.append('?')
          found = true
        }
      }
      index += 1
    }
    asciibuf.toString()
  }

  private def decodeTriple(str: String): Point = {
    val c1: Byte = decode_chars(str.charAt(0).asInstanceOf[Int]).asInstanceOf[Byte]
    val x: Int = fastDecode(str.substring(1))
    if (c1 < 24) {
      return Point.fromMicroDeg(c1 / 6 * 34 + x % 34, (c1 % 6) * 28 + x / 34)
    }
    Point.fromMicroDeg(x % 40 + 136, x / 40 + 24 * (c1 - 24))
  }

  private def decode6(v: Int, width: Int, height: Int): Point = {
    var d: Int = 6
    var col: Int = v / (height * 6)
    val maxcol: Int = (width - 4) / 6
    if (col >= maxcol) {
      col = maxcol
      d = width - maxcol * 6
    }
    val w: Int = v - col * height * 6
    Point.fromMicroDeg(height - 1 - w / d, col * 6 + w % d)
  }

  private def fastDecode(code: String): Int = {
    var value: Int = 0
    var i: Int = 0
    i = 0
    var done = false
    while (!done && i < code.length) {
      val c: Int = code.charAt(i).asInstanceOf[Int]
      if (c == 46) {
        done = true
      } else {
        if (decode_chars(c) < 0) {
          value = -1
          done = true
        }
        value = value * 31 + decode_chars(c)
        i += 1
      }
    }

    value
  }

  private def add2res(y: Int, x: Int, dividerx4: Int, dividery: Int, ydirection: Int, extrapostfix: String): Point = {
    if (extrapostfix.isEmpty) {
      Point.fromMicroDeg(y + (dividery / 2) * ydirection, x + dividerx4 / 8)
    } else {
      var c1: Int = extrapostfix.charAt(0).asInstanceOf[Int]
      c1 = decode_chars(c1)
      if (c1 < 0) {
        c1 = 0
      }
      else if (c1 > 29) {
        c1 = 29
      }
      val y1: Int = c1 / 5
      val x1: Int = c1 % 5
      var c2: Int = if (extrapostfix.length == 2) extrapostfix.charAt(1).asInstanceOf[Int] else 72
      c2 = decode_chars(c2)
      if (c2 < 0) {
        c2 = 0
      }
      else if (c2 > 29) {
        c2 = 29
      }
      val y2: Int = c2 / 6
      val x2: Int = c2 % 6
      val extrax: Int = ((x1 * 12 + 2 * x2 + 1) * dividerx4 + 120) / 240
      val extray: Int = ((y1 * 10 + 2 * y2 + 1) * dividery + 30) / 60
      Point.fromMicroDeg(y + extray * ydirection, x + extrax)
    }
  }

}

case class Unicode2Ascii(min: Int, max: Int, convert: String)


