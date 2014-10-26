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
 * This class defines a single mapcode encoding result, including the mapcode itself and the
 * territory definition.
 *
 * Note that the constructor will throw an `IllegalArgumentException` if the syntax of the mapcode
 * is not correct. The mapcode is not checked for validity, other than its syntax.
 *
 * @param mapcodePrecision0  The Mapcode string (without territory information) with standard precision.
 *                           The returned mapcode does not include the '-' separator and additional digits.
 *                           The precision is approximately 5 meters. The precision is defined as the maximum distance
 *                           to the (latitude, longitude) pair that encoded to this mapcode, which means the mapcode
 *                           defines an area of approximately 10 x 10 meters (100 m2).
 * @param mapcodePrecision1  The medium-precision mapcode string (without territory information).
 *                           The returned mapcode includes the '-' separator and 1 additional digit, if available.
 *                           If a medium precision code is not available, the regular mapcode is returned.
 *                           The returned precision is approximately 1 meter. The precision is defined as the maximum
 *                           distance to the (latitude, longitude) pair that encoded to this mapcode, which means the
 *                           mapcode defines an area of approximately 2 x 2 meters (4 m2).
 * @param mapcodePrecision2  The high-precision mapcode string (without territory information).
 *                           The returned mapcode includes the '-' separator and 2 additional digits, if available.
 *                           If a high precision code is not available, the regular mapcode is returned.
 *                           The returned precision is approximately 16 centimeters. The precision is defined as the
 *                           maximum distance to the (latitude, longitude) pair that encoded to this mapcode, which
 *                           means the mapcode defines an area of approximately 32 x 32 centimeters (0.1 m2).
 * @param territory          The territory in which this mapcode can be found, typically either a country or a state or
 *                           province.
 */

case class Mapcode private[scala](mapcodePrecision0: String,
                                  mapcodePrecision1: String,
                                  mapcodePrecision2: String,
                                  territory: Territory.Territory) {

  def mapcode: String = mapcodePrecision0

  /**
   * Return the local mapcode string, potentially ambiguous.
   *
   * Example:
   * 49.4V
   *
   * @return Local mapcode.
   */
  def asLocal: String = mapcodePrecision0

  /**
   * Return the full international mapcode, including the full name of the territory and the Mapcode itself.
   * The format of the code is:
   * full-territory-name mapcode
   *
   * Example:
   * Netherlands 49.4V           (regular code)
   * Netherlands 49.4V-K2        (high precision code)
   *
   * @return Full international mapcode.
   */
  def asInternationalFullName: String = s"${territory.fullName} $mapcodePrecision0"

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
   * @return Short-hand international mapcode.
   */
  def asInternationalISO: String = s"${territory.name} $mapcodePrecision0"
}

object Mapcode {

  /**
   * These patterns and matchers are used internally in this module to match mapcodes. They are
   * provided as statics to only compile these patterns once.
   */
  val PrecisionRx = """[-][\p{Alpha}\p{Digit}&&[^zZ]]{1,2}+""".r

  /**
   * This patterns/regular expressions is used for checking mapcode format strings.
   * They've been made pulkic to allow others to use the correct regular expressions as well.
   */
  val FormatRx = s"""^[\\p{Alpha}\\p{Digit}]{2,5}+[.][\\p{Alpha}\\p{Digit}]{2,5}+($PrecisionRx)?$$""".r

  private[scala] def apply(mapcode: String, territory: Territory.Territory): Mapcode = {
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

