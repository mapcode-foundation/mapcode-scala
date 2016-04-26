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

import scala.language.implicitConversions
import com.mapcode.{Alphabet => JAlphabet}

/**
 * This enum defines all alphabets supported for mapcodes. Mapcodes can be safely converted between
 * alphabets and fed to the mapcode decoder in the regular ASCII Roman alphabet or any other.
 */
case class Alphabet private (delegate: JAlphabet) {

  Alphabet.count += 1

  override def toString =
    delegate.toString
}

object Alphabet {

  val ROMAN = Alphabet(JAlphabet.ROMAN)
  val GREEK = Alphabet(JAlphabet.GREEK)
  val CYRILLIC = Alphabet(JAlphabet.CYRILLIC)
  val HEBREW = Alphabet(JAlphabet.HEBREW)
  val HINDI = Alphabet(JAlphabet.HINDI)
  val MALAY = Alphabet(JAlphabet.MALAY)
  val GEORGIAN = Alphabet(JAlphabet.GEORGIAN)
  val KATAKANA = Alphabet(JAlphabet.KATAKANA)
  val THAI = Alphabet(JAlphabet.THAI)
  val LAO = Alphabet(JAlphabet.LAO)
  val ARMENIAN = Alphabet(JAlphabet.ARMENIAN)
  val BENGALI = Alphabet(JAlphabet.BENGALI)
  val GURMUKHI = Alphabet(JAlphabet.GURMUKHI)
  val TIBETAN = Alphabet(JAlphabet.TIBETAN)

  val values: Seq[Alphabet] =
    JAlphabet.values.map(Alphabet(_))

  implicit def fromJava(delegate: JAlphabet): Alphabet =
    Alphabet(delegate)

  implicit def toJava(alphabet: Alphabet): JAlphabet =
    alphabet.delegate

  private var count: Int = 0
  assert (count == values.size)
}
