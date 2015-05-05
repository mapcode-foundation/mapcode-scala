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

import com.mapcode.scala.CheckArgs.{checkNonnull, checkRange}
import com.mapcode.scala.Common.{countCityCoordinatesForCountry, getFirstNamelessRecord, nc, xDivider, xSide, ySide}

import scala.collection.mutable.ArrayBuffer

private[scala] object Encoder {
  private final val encode_chars: Array[Char] =
    Array('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'B', 'C', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N',
      'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'X', 'Y', 'Z')

  private[scala] def encode(latDeg: Double,
                            lonDeg: Double,
                            territory: Option[Territory.Territory],
                            isRecursive: Boolean,
                            limitToOneResult: Boolean,
                            allowWorld: Boolean,
                            stateOverride: Option[Territory.Territory] = None): Seq[Mapcode] = {
    checkRange("latDeg", latDeg, Point.LAT_DEG_MIN, Point.LAT_DEG_MAX)
    checkRange("lonDeg", lonDeg, Point.LON_DEG_MIN, Point.LON_DEG_MAX)
    checkNonnull("restrictToTerritory", territory)
    val pointToEncode = Point.fromDeg(latDeg, if (lonDeg > 179.999999) lonDeg - 360 else lonDeg)
    val areas: Seq[SubArea] = SubArea.getAreasForPoint(pointToEncode)
    val results = ArrayBuffer[Mapcode]()
    var lastbasesubareaID: Int = -1
    for (subArea <- areas) {
      if ((!limitToOneResult || results.isEmpty) && territory.fold(true)(_ == subArea.parentTerritory)) {
        val currentEncodeTerritory: Territory.Territory = subArea.parentTerritory
        if (currentEncodeTerritory != Territory.AAA || allowWorld || territory.fold(false)(_ == Territory.AAA)) {
          val from: Int = DataAccess.dataFirstRecord(currentEncodeTerritory.territoryCode)
          var mapcoderData = Data(from)
          if (mapcoderData.flags != 0) {
            val upto: Int = DataAccess.dataLastRecord(currentEncodeTerritory.territoryCode)
            val i: Int = subArea.subAreaId
            mapcoderData = Data(i)
            val Some(mapcoderRect: SubArea) = mapcoderData.mapcoderRect
            if (mapcoderData.codex < 54 && mapcoderRect.containsPoint(pointToEncode)) {
              var mapcode = Option.empty[String]
              val territoryParent = currentEncodeTerritory.parentTerritory
              if (mapcoderData.useless && i == upto && territoryParent.isDefined) {
                if (!isRecursive) {
                  results ++= encode(pointToEncode.latDeg, pointToEncode.lonDeg, territoryParent,
                    isRecursive = true, limitToOneResult = limitToOneResult, allowWorld = allowWorld, Option(currentEncodeTerritory))
                }
              } else {
                if (mapcoderData.pipeType == 0 && !mapcoderData.nameless) {
                  if (!mapcoderData.useless || lastbasesubareaID == from) {
                    mapcode = encodeGrid(i, pointToEncode, mapcoderData)
                  }
                }
                else if (mapcoderData.pipeType == 4) {
                  mapcode = encodeGrid(i, pointToEncode, mapcoderData)
                }
                else if (mapcoderData.nameless) {
                  mapcode = encodeNameless(pointToEncode, mapcoderData, i, from)
                }
                else {
                  mapcode = encodeStarpipe(pointToEncode, mapcoderData, i)
                }

                if (mapcode.isDefined && mapcode.get.length > 4) {
                  mapcode = aeuPack(mapcode.get)
                  var encodeTerritory: Territory.Territory = currentEncodeTerritory
                  if (stateOverride.isDefined) {
                    encodeTerritory = stateOverride.get
                  }
                  val newResult: Mapcode = Mapcode(mapcode.get, encodeTerritory)
                  if (!results.contains(newResult)) {
                    if (limitToOneResult) {
                      results.clear()
                    }
                    results += newResult
                  }
                  else {
                    sys.error(s"encode: Duplicate results found, newResult=${newResult.asInternationalISO}, " +
                      s"results=${results.size} items")
                  }
                  lastbasesubareaID = from
                }
              }
            }
          }
        }
      }
    }
    results // may be empty if coords not within territory
  }

  private def encodeGrid(m: Int, point: Point, mapcoderData: Data): Option[String] = {
    var pointToEncode: Point = point
    var codex: Int = mapcoderData.codex
    val orgcodex: Int = codex
    if (codex == 14) {
      codex = 23
    }
    val dc = codex / 10
    val codexlow = codex % 10
    var divy = DataAccess.smartDiv(m)
    val divx = if (divy == 1) {
      divy = ySide(dc)
      xSide(dc)
    }
    else {
      nc(dc) / divy
    }
    val Some(mapcoderRect) = mapcoderData.mapcoderRect
    val ygridsize = (mapcoderRect.maxY - mapcoderRect.minY + divy - 1) / divy
    var rely = (pointToEncode.latMicroDeg - mapcoderRect.minY) / ygridsize
    val xgridsize = (mapcoderRect.maxX - mapcoderRect.minX + divx - 1) / divx
    var relx = pointToEncode.lonMicroDeg - mapcoderRect.minX
    while (relx < 0) {
      pointToEncode = Point.fromMicroDeg(pointToEncode.latMicroDeg, pointToEncode.lonMicroDeg + 360000000)
      relx += 360000000
    }
    while (relx >= 360000000) {
      pointToEncode = Point.fromMicroDeg(pointToEncode.latMicroDeg, pointToEncode.lonMicroDeg - 360000000)
      relx -= 360000000
    }
    relx = relx / xgridsize
    val v = if (divx != divy && codex > 24) {
      encode6(relx, rely, divx, divy)
    }
    else {
      relx * divy + divy - 1 - rely
    }
    var result: String = fastEncode(v, dc)
    if (dc == 4 && divx == xSide(4) && divy == ySide(4)) {
      result = String.valueOf(result.charAt(0)) + result.charAt(2) + result.charAt(1) + result.charAt(3)
    }

    rely = mapcoderRect.minY + rely * ygridsize
    relx = mapcoderRect.minX + relx * xgridsize
    val dividery = (ygridsize + ySide(codexlow) - 1) / ySide(codexlow)
    val dividerx = (xgridsize + xSide(codexlow) - 1) / xSide(codexlow)
    result += '.'
    val nrchars = codexlow
    var difx = pointToEncode.lonMicroDeg - relx
    var dify = pointToEncode.latMicroDeg - rely
    val extrax = difx % dividerx
    val extray = dify % dividery
    difx = difx / dividerx
    dify = dify / dividery
    dify = ySide(nrchars) - 1 - dify
    if (nrchars == 3) {
      result += encodeTriple(difx, dify)
    }
    else {
      var postfix: String = fastEncode(difx * ySide(nrchars) + dify, nrchars)
      if (nrchars == 4) {
        postfix = String.valueOf(postfix.charAt(0)) + postfix.charAt(2) + postfix.charAt(1) + postfix.charAt(3)
      }
      result += postfix
    }
    if (orgcodex == 14) {
      result = result.charAt(0) + "." + result.charAt(1) + result.substring(3)
    }
    result += addPostfix(extrax << 2, extray, dividerx << 2, dividery)
    if (result.nonEmpty) Some(mapcoderData.pipeLetter.getOrElse("") + result)
    else None
  }

  private def encodeStarpipe(pointToEncode: Point, argMapcoderData: Data, thisindex: Int): Option[String] = {
    var mapcoderData = argMapcoderData
    val starpipe_result = new StringBuilder
    val thiscodexlen = mapcoderData.codexLen
    var done = false
    var storageStart = 0
    var firstindex = thisindex
    while (Data.calcStarPipe(firstindex - 1) && Data.calcCodexLen(firstindex - 1) == thiscodexlen) {
      firstindex -= 1
    }
    var i = firstindex
    while (!done) {
      if (Data.calcCodexLen(i) != thiscodexlen) {
        done = true
      } else {
        mapcoderData = Data(i)
        if (!done) {
          val Some(mapcoderRect) = mapcoderData.mapcoderRect
          val maxx: Int = mapcoderRect.maxX
          val maxy: Int = mapcoderRect.maxY
          val minx: Int = mapcoderRect.minX
          val miny: Int = mapcoderRect.minY
          var h: Int = (maxy - miny + 89) / 90
          val xdiv: Int = xDivider(miny, maxy)
          var w: Int = ((maxx - minx) * 4 + xdiv - 1) / xdiv
          h = 176 * ((h + 176 - 1) / 176)
          w = 168 * ((w + 168 - 1) / 168)
          var product: Int = (w / 168) * (h / 176) * 961 * 31
          val goodRounder: Int = if (mapcoderData.codex >= 23) 961 * 961 * 31 else 961 * 961
          if (mapcoderData.pipeType == 8) {
            product = ((storageStart + product + goodRounder - 1) / goodRounder) * goodRounder - storageStart
          }
          if (i == thisindex && mapcoderRect.containsPoint(pointToEncode)) {
            val dividerx: Int = (maxx - minx + w - 1) / w
            var vx: Int = (pointToEncode.lonMicroDeg - minx) / dividerx
            val extrax: Int = (pointToEncode.lonMicroDeg - minx) % dividerx
            val dividery: Int = (maxy - miny + h - 1) / h
            var vy: Int = (maxy - pointToEncode.latMicroDeg) / dividery
            val extray: Int = (maxy - pointToEncode.latMicroDeg) % dividery
            val spx: Int = vx % 168
            val spy: Int = vy % 176
            vx = vx / 168
            vy = vy / 176
            val value: Int = vx * (h / 176) + vy
            starpipe_result.append(fastEncode(storageStart / (961 * 31) + value, mapcoderData.codexLen - 2))
            starpipe_result.append('.')
            starpipe_result.append(encodeTriple(spx, spy))
            starpipe_result.append(addPostfix(extrax << 2, extray, dividerx << 2, dividery))
            done = true
          }
          storageStart += product
        }
      }
      i += 1
    }
    if (starpipe_result.isEmpty) None
    else Some(starpipe_result.toString())
  }

  private def addPostfix(extrax4: Int, extray: Int, dividerx4: Int, dividery: Int): String = {
    val gx: Int = (30 * extrax4) / dividerx4
    val gy: Int = (30 * extray) / dividery
    val x1: Int = gx / 6
    val y1: Int = gy / 5
    var s: String = "-" + encode_chars(y1 * 5 + x1)
    val x2: Int = gx % 6
    val y2: Int = gy % 5
    s += encode_chars(y2 * 6 + x2)
    s
  }

  private def encodeTriple(difx: Int, dify: Int): String = {
    if (dify < 4 * 34) {
      encode_chars(difx / 28 + 6 * (dify / 34)) + fastEncode((difx % 28) * 34 + dify % 34, 2)
    }
    else {
      encode_chars(difx / 24 + 24) + fastEncode((difx % 24) * 40 + dify - 136, 2)
    }
  }

  private def fastEncode(argValue: Int, argNrChars: Int): String = {
    var value: Int = argValue
    var nrChars: Int = argNrChars
    val result: StringBuilder = new StringBuilder
    while (nrChars > 0) {
      nrChars -= 1
      result.insert(0, encode_chars(value % 31))
      value = value / 31
    }
    result.toString()
  }

  private def encodeNameless(pointToEncode: Point, mapcoderData: Data, index: Int, firstcode: Int): Option[String] = {
    val first_nameless_record: Int = getFirstNamelessRecord(mapcoderData.codex, index, firstcode)
    val a: Int = countCityCoordinatesForCountry(mapcoderData.codex, index, firstcode)
    val p: Int = 31 / a
    val r: Int = 31 % a
    val nrX: Int = index - first_nameless_record
    val Some(mapcoderRect) = mapcoderData.mapcoderRect
    val maxy: Int = mapcoderRect.maxY
    val minx: Int = mapcoderRect.minX
    val miny: Int = mapcoderRect.minY
    val x: Int = pointToEncode.lonMicroDeg
    val y: Int = pointToEncode.latMicroDeg
    if (a > 1) {
      var storage_offset: Int = 0
      if (mapcoderData.codex != 21 && a <= 31) {
        storage_offset = (nrX * p + (if (nrX < r) nrX else r)) * (961 * 961)
      }
      else if (mapcoderData.codex != 21 && a < 62) {
        if (nrX < 62 - a) {
          storage_offset = nrX * 961 * 961
        }
        else {
          storage_offset = (62 - a + (nrX - 62 + a) / 2) * 961 * 961
          if (((nrX + a) & 1) != 0) {
            storage_offset += 16 * 961 * 31
          }
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
        storage_offset = nrX * basePowerA
      }
      var side: Int = DataAccess.smartDiv(index)
      val orgSide: Int = side
      var xSide: Int = side
      if (mapcoderData.specialShape) {
        xSide *= side
        side = 1 + (maxy - miny) / 90
        xSide = xSide / side
      }
      val dividerx4: Int = xDivider(miny, maxy)
      val dx: Int = (4 * (x - minx)) / dividerx4
      val extrax4: Int = (x - minx) * 4 - (dx * dividerx4)
      val dividery: Int = 90
      val dy: Int = (maxy - y) / dividery
      val extray: Int = (maxy - y) % dividery
      var v: Int = storage_offset
      if (mapcoderData.specialShape) {
        v += encode6(dx, side - 1 - dy, xSide, side)
      }
      else {
        v += dx * side + dy
      }
      var result: String = fastEncode(v, mapcoderData.codexLen + 1)
      if (mapcoderData.codexLen == 3) {
        result = result.substring(0, 2) + '.' + result.substring(2)
      }
      else if (mapcoderData.codexLen == 4) {
        if (mapcoderData.codex == 22 && a < 62 && orgSide == 961 && !mapcoderData.specialShape) {
          result = result.substring(0, 2) + result.charAt(3) + result.charAt(2) + result.charAt(4)
        }
        if (mapcoderData.codex == 13) {
          result = result.substring(0, 2) + '.' + result.substring(2)
        }
        else {
          result = result.substring(0, 3) + '.' + result.substring(3)
        }
      }
      result += addPostfix(extrax4, extray, dividerx4, dividery)
      Some(result)
    } else None
  }

  private def aeuPack(argStr: String): Option[String] = {
    var str: String = argStr
    var dotpos: Int = -9
    var rlen: Int = str.length
    var d: Int = 0
    var done = false
    var rest = Option.empty[String]
    d = 0
    var result = ""
    while (!done && d < rlen) {
      if (str.charAt(d) < '0' || str.charAt(d) > '9') {
        if (str.charAt(d) == '.' && dotpos < 0) {
          dotpos = d
        }
        else if (str.charAt(d) == '-') {
          rest = Some(str.substring(d))
          str = str.substring(0, d)
          rlen = d
        }
        else {
          result = str
          done = true
        }
      }
      d += 1
    }
    if (!done) {
      if (rlen - 2 > dotpos) {
        val v: Int = (str.charAt(rlen - 2).asInstanceOf[Int] - 48) * 10 + str.charAt(rlen - 1).asInstanceOf[Int] - 48
        val last: Int = v % 34
        val vowels: Array[Char] = Array('A', 'E', 'U')
        str = str.substring(0, rlen - 2) + vowels(v / 34) + (if (last < 31) encode_chars(last) else vowels(last - 31))
      }
      Some(str + rest.getOrElse(""))
    } else if (result.isEmpty) None else Some(result)
  }

  private def encode6(x: Int, y: Int, width: Int, height: Int): Int = {
    var d: Int = 6
    var col: Int = x / 6
    val maxcol: Int = (width - 4) / 6
    if (col >= maxcol) {
      col = maxcol
      d = width - maxcol * 6
    }
    height * 6 * col + (height - 1 - y) * d + x - col * 6
  }

}


