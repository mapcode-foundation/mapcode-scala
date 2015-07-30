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

import com.mapcode.{Mapcode => JMapCode, Territory, Alphabet}

/**
 * This class defines a single mapcode encoding result, including the alphanumeric code and the
 * territory definition.
 *
 * On terminology, mapcode territory and mapcode code:
 *
 * In written form. a mapcode is defined as an alphanumeric code, optionally preceded by a
 * territory code.
 *
 * For example: "NLD 49.4V" is a mapcode, but "49.4V" is a mapcode as well, The latter is called
 * a "local" mapcode, because it is not internationally unambiguous unless preceded by a territory
 * code.
 *
 * For "NLD 49.4V" the "NLD"-part is called "the territory" and the "49.4V"-part is called
 * "the code" (which are both part of "the mapcode").
 *
 * This distinction between "territory" and "code" in a mapcode is why the interface of this class
 * has been changed from version 1.50.0 to reflect this terminology.
 *
 * On alphabets:
 *
 * Mapcode codes can be represented in different alphabets. Note that an alphabet is something else
 * than a locale or a language. The supported alphabets for mapcodes are listed in [[com.mapcode.Alphabet]].
 *
 * Mapcode objects provide methods to obtain the mapcode code in a specific alphabet. By default,
 * the [[com.mapcode.Alphabet#ROMAN]] is used.
 *
 * @param code      Code of mapcode.
 * @param territory Territory.
 */
case class Mapcode(code: String, territory: Territory) {

  private val delegate = new JMapCode(code, territory)

  /**
   * Get the Mapcode string (without territory information) with standard precision.
   * The returned mapcode does not include the '-' separator and additional digits.
   *
   * The returned precision is approximately 5 meters. The precision is defined as the maximum distance to the
   * (latitude, longitude) pair that encoded to this mapcode, which means the mapcode defines an area of
   * approximately 10 x 10 meters (100 m2).
   *
   * @param alphabet Alphabet.
   * @return Mapcode string.
   */
  def code(alphabet: Alphabet): String =
    delegate.getCode(alphabet)

  def code(precision: Int, alphabet: Alphabet): String =
    delegate.getCode(precision, alphabet)

  def code(precision: Int): String =
    delegate.getCode(precision)

  /**
   * Return the full international mapcode, including the full name of the territory and the mapcode code itself.
   * The format of the string is:
   * full-territory-name cde
   *
   * Example:
   * Netherlands 49.4V           (regular code)
   * Netherlands 49.4V-K2        (high precision code)
   *
   * @param precision Precision specifier. Range: [0, 2].
   * @param alphabet  Alphabet.
   * @return Full international mapcode.
   * @throws IllegalArgumentException Thrown if precision is out of range (must be in [0, 2]).
   */
  def codeWithTerritoryFullname(precision: Int, alphabet: Alphabet): String =
    delegate.getCodeWithTerritoryFullname(precision, alphabet)

  def codeWithTerritoryFullname(precision: Int): String =
    delegate.getCodeWithTerritoryFullname(precision)

  def codeWithTerritoryFullname(alphabet: Alphabet): String =
    delegate.getCodeWithTerritoryFullname(alphabet)

  def codeWithTerritoryFullname: String =
    delegate.getCodeWithTerritoryFullname

  /**
   * Return the international mapcode as a shorter version using the ISO territory codes where possible.
   * International codes use a territory code "AAA".
   * The format of the code is:
   * short-territory-name mapcode
   *
   * Example:
   * NLD 49.4V                   (regular code)
   * NLD 49.4V-K2                (high-precision code)
   *
   * @param precision Precision specifier. Range: [0, 2].
   * @param alphabet  Alphabet.
   * @return Short-hand international mapcode.
   * @throws IllegalArgumentException Thrown if precision is out of range (must be in [0, 2]).
   */
  def codeWithTerritory(precision: Int, alphabet: Alphabet): String =
    delegate.getCodeWithTerritory(precision, alphabet)

  def codeWithTerritory(precision: Int): String =
    delegate.getCodeWithTerritory(precision)

  def codeWithTerritory(alphabet: Alphabet): String =
    delegate.getCodeWithTerritory(alphabet)

  def codeWithTerritory: String =
    delegate.getCodeWithTerritory
}

object Mapcode {

  /**
   * These patterns and matchers are used internally in this module to match mapcodes. They are
   * provided as statics to only compile these patterns once.
   */
  private[scala] val PrecisionRx = """[-][\p{Alpha}\p{Digit}&&[^zZ]]{1,2}+""".r

  /**
   * This patterns/regular expressions is used for checking mapcode format strings.
   * It's been made public to allow others to use the correct regular expressions as well.
   */
  val FormatRx = s"""^[\\p{Alpha}\\p{Digit}]{2,5}+[.][\\p{Alpha}\\p{Digit}]{2,5}+($PrecisionRx)?$$""".r

  private[scala] def apply(mapcode: String, territory: Territory.Territory): Mapcode = {
    // todo -- this code is wrong if a p1 mapcode is passed in here; only p0 or p2
    require(isValidMapcodeFormat(mapcode),
      s"$mapcode is not correctly formatted; the regex for the syntax is $FormatRx")
    val p2 = mapcode
    val (p0, p1) =
      if (mapcode.contains("-")) (mapcode.substring(0, mapcode.length - 3), mapcode.substring(0, mapcode.length - 1))
      else (p2, p2)
    new Mapcode(mapcodePrecision0 = p0, mapcodePrecision1 = p1, mapcodePrecision2 = p2, territory = territory)
  }

  /**
   * This method provides a shortcut to checking if a mapcode string is formatted properly or not at all.
   *
   * @param mapcode Mapcode string.
   * @return True if the mapcode format, the syntax, is correct. This does not mean the mapcode is actually a valid
   *         mapcode representing a location on Earth.
   */
  def isValidMapcodeFormat(mapcode: String): Boolean = getMapcodeFormatType(mapcode) != MapcodeFormat.Invalid

  /**
   * This method return the mapcode type, given a mapcode string. If the mapcode string has an invalid
   * format, `MapcodeFormatType#Invalid` is returned. If another value is returned,
   * the precision of the mapcode is given.
   *
   * Note that this method only checks the syntactic validity of the mapcode, the string format. It does not
   * check if the mapcode is really a valid mapcode representing a position on Earth.
   *
   * @param mapcode Mapcode string.
   * @return Type of mapcode format, or { @link MapcodeFormatType#Invalid} if not valid.
   */
  def getMapcodeFormatType(mapcode: String): MapcodeFormat.MapcodeFormat = {
    convertToAscii(mapcode) match {
      case FormatRx(precision) if precision == null => MapcodeFormat.Precision0
      case FormatRx(precision) if precision.size == 2 => MapcodeFormat.Precision1
      case FormatRx(precision) if precision.size == 3 => MapcodeFormat.Precision2
      case _ => MapcodeFormat.Invalid
    }
  }

  /**
   * Convert a mapcode which potentially contains Unicode characters, to an ASCII veriant.
   *
   * @param mapcode Mapcode, with optional Unicode characters.
   * @return ASCII, non-Unicode string.
   */
  def convertToAscii(mapcode: String): String = Decoder.decodeUTF16(mapcode)

  /**
   * This enum describes the types of mapcodes available.
   */
  object MapcodeFormat extends Enumeration {
    type MapcodeFormat = Value
    val Invalid, Precision0, Precision1, Precision2 = Value
  }

}

