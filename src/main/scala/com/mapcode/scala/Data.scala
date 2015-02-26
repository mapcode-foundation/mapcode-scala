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

/**
 * This class the data class for Mapcode codex items.
 */
private[scala] case class Data(flags: Int,
                               codex: Int,
                               codexLo: Int,
                               codexHi: Int,
                               codexLen: Int,
                               nameless: Boolean,
                               useless: Boolean,
                               specialShape: Boolean,
                               pipeType: Int,
                               pipeLetter: Option[String],
                               starPipe: Boolean,
                               mapcoderRect: Option[SubArea])

private[scala] object Data {
  val ENCODE_CHARS: Array[Char] = Array(
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
    'B', 'C', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 'M',
    'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'X', 'Y', 'Z')

  def calcCodex(i: Int): Int = {
    val flags = DataAccess.dataFlags(i)
    calcCodex(calcCodexHi(flags), calcCodexLo(flags))
  }

  def calcCodexHi(flags: Int): Int = (flags & 31) / 5

  def calcCodexLo(flags: Int): Int = ((flags & 31) % 5) + 1

  def calcCodex(codexhi: Int, codexlo: Int): Int = (10 * codexhi) + codexlo

  def calcCodexLen(i: Int): Int = {
    val flags: Int = DataAccess.dataFlags(i)
    calcCodexLen(calcCodexHi(flags), calcCodexLo(flags))
  }

  def calcCodexLen(codexhi: Int, codexlo: Int): Int = codexhi + codexlo

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
    val pipeLetter =
      if (pipeType == 4) Some(Character.toString(ENCODE_CHARS((flags >> 11) & 31)))
      else None

    if ((codex == 21) && !nameless) {
      codex += 1
      codexLo += 1
      codexLen += 1
    }
    val starPipe = calcStarPipe(i)
    val mapcoderRect = SubArea.getArea(i)

    new Data(flags = flags, codex = codex, codexLo = codexLo, codexHi = codexHi, codexLen = codexLen,
      nameless = nameless, useless = useless, specialShape = specialShape, pipeType = pipeType, pipeLetter = pipeLetter,
      starPipe = starPipe, mapcoderRect = mapcoderRect)
  }

  def isNameless(i: Int): Boolean = (DataAccess.dataFlags(i) & 64) != 0

  def isSpecialShape(i: Int): Boolean = (DataAccess.dataFlags(i) & 1024) != 0

  def calcStarPipe(i: Int): Boolean = (DataAccess.dataFlags(i) & (8 << 5)) != 0
}




