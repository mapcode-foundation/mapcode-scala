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

/**
 * This class the data class for Mapcode codex items.
 */
private [scala] object Data {
  def isNameless(i: Int): Boolean = (DataAccess.dataFlags(i) & 64) != 0

  def isSpecialShape(i: Int): Boolean = (DataAccess.dataFlags(i) & 1024) != 0

  def calcCodex(i: Int): Int = {
    val flags = DataAccess.dataFlags(i)
    calcCodex(calcCodexHi(flags), calcCodexLo(flags))
  }

  def calcCodexLen(i: Int): Int = {
    val flags: Int = DataAccess.dataFlags(i)
    calcCodexLen(calcCodexHi(flags), calcCodexLo(flags))
  }

  def calcStarPipe(i: Int): Boolean =  (DataAccess.dataFlags(i) & (8 << 5)) != 0

  def calcCodexHi(flags: Int): Int =  (flags & 31) / 5

  def calcCodexLo(flags: Int): Int = ((flags & 31) % 5) + 1

  def calcCodex(codexhi: Int, codexlo: Int): Int =  (10 * codexhi) + codexlo

  def calcCodexLen(codexhi: Int, codexlo: Int): Int = codexhi + codexlo

  val ENCODE_CHARS: Array[Char] = Array(
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
    'B', 'C', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 'M',
    'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'X', 'Y', 'Z')
}

/*
class Data {
  private[mapcode] def getFlags: Int = {
    assert(initialized)
    return flags
  }

  private[mapcode] def getCodex: Int = {
    assert(initialized)
    return codex
  }

  private[mapcode] def getCodexLo: Int = {
    assert(initialized)
    return codexLo
  }

  private[mapcode] def getCodexHi: Int = {
    assert(initialized)
    return codexHi
  }

  private[mapcode] def getCodexLen: Int = {
    assert(initialized)
    return codexLen
  }

  private[mapcode] def isNameless: Boolean = {
    assert(initialized)
    return nameless
  }

  private[mapcode] def isUseless: Boolean = {
    assert(initialized)
    return useless
  }

  private[mapcode] def isSpecialShape: Boolean = {
    assert(initialized)
    return specialShape
  }

  private[mapcode] def getPipeType: Int = {
    assert(initialized)
    return pipeType
  }

  @Nonnull private[mapcode] def getPipeLetter: String = {
    assert(initialized)
    assert(pipeLetter != null)
    return pipeLetter
  }

  private[mapcode] def isStarPipe: Boolean = {
    assert(initialized)
    return starPipe
  }

  @Nonnull private[mapcode] def getMapcoderRect: SubArea = {
    assert(initialized)
    assert(mapcoderRect != null)
    return mapcoderRect
  }

  def apply(i: Int): Data = {
    val flags = DataAccess.dataFlags(i)
    val codexHi = calcCodexHi(flags)
    var codexLo = calcCodexLo(flags)
    var codexLen = calcCodexLen(codexHi, codexLo)
    var codex = calcCodex(codexHi, codexLo)
    val nameless = isNameless(i)
    val useless = (flags & 512) != 0
    val specialShape = isSpecialShape(i)
    val pipeType = (flags >> 5) & 12
    val pipeLetter = if (pipeType == 4) {
      Character.toString(ENCODE_CHARS((flags >> 11) & 31))
    } else ""

    if ((codex == 21) && !nameless) {
      codex += 1
      codexLo += 1
      codexLen += 1
    }
    starPipe = calcStarPipe(i)
    mapcoderRect = SubArea.getArea(i)

  }
  private[mapcode] def dataSetup(i: Int) {
    initialized = true
  }

}
*/

case class Data(flags: Int, codex: Int, codexLo: Int, codeHi: Int, codexLen: Int, nameless: Boolean, useless: Boolean,
                 specialShape: Boolean, pipeType: Int, pipeLetter: Option[String], starPipe: Boolean, mapcoderRect: Option[SubArea])

