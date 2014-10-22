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
 * Note that the constructor will throw an {@link IllegalArgumentException} if the syntax of the mapcode
 * is not correct. The mapcode is not checked for validity, other than its syntax.
 *
 * @param mapcodePrecision0 the Mapcode string (without territory information) with standard precision.
 *                          The returned mapcode does not include the '-' separator and additional digits.
 *                          The precision is approximately 5 meters. The precision is defined as the maximum distance
 *                          to the (latitude, longitude) pair that encoded to this mapcode, which means the mapcode
 *                          defines an area of approximately 10 x 10 meters (100 m2).
 * @param mapcodePrecision1 Get the medium-precision mapcode string (without territory information).
 *                          The returned mapcode includes the '-' separator and 1 additional digit, if available.
 *                          If a medium precision code is not available, the regular mapcode is returned.
 *                          The returned precision is approximately 1 meter. The precision is defined as the maximum
 *                          distance to the (latitude, longitude) pair that encoded to this mapcode, which means the
 *                          mapcode defines an area of approximately 2 x 2 meters (4 m2).
 * @param mapcodePrecision2 the high-precision mapcode string (without territory information).
 *                          The returned mapcode includes the '-' separator and 2 additional digit2, if available.
 *                          If a high precision code is not available, the regular mapcode is returned.
 *                          The returned precision is approximately 16 centimeters. The precision is defined as the
 *                          maximum distance to the (latitude, longitude) pair that encoded to this mapcode, which
 *                          means the mapcode defines an area of approximately 32 x 32 centimeters (0.1 m2).
 */

case class Mapcode(mapcodePrecision0: String,
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
  val REGEX_MAPCODE_FORMAT1 = "^[\\p{Alpha}\\p{Digit}]{2,5}+"
  val REGEX_MAPCODE_FORMAT2 = "[.][\\p{Alpha}\\p{Digit}]{2,5}+"
  val REGEX_MAPCODE_PRECISION = "[-][\\p{Alpha}\\p{Digit}&&[^zZ]]{1,2}+"

  /**
   * This patterns/regular expressions is used for checking mapcode format strings.
   * They've been made pulkic to allow others to use the correct regular expressions as well.
   */
  val REGEX_MAPCODE_FORMAT: String = s"$REGEX_MAPCODE_FORMAT1$REGEX_MAPCODE_FORMAT2($REGEX_MAPCODE_PRECISION)?$$"
  private val PATTERN_MAPCODE_FORMAT = REGEX_MAPCODE_FORMAT.r
  private val PATTERN_MAPCODE_PRECISION = REGEX_MAPCODE_PRECISION.r

  def apply(mapcode: String, territory: Territory.Territory) {
    require(isValidMapcodeFormat(mapcode),
      s"$mapcode is not correctly formatted; the regex for the syntax is $REGEX_MAPCODE_FORMAT")
    val mapcodePrecision2 = mapcode
    val (mapcodePrecision0, mapcodePrecision1) = if (mapcode.contains("-")) {
      (mapcode.substring(0, mapcode.length - 3), mapcode.substring(0, mapcode.length - 1))
    }
    else {
      (mapcode, mapcode)
    }
    new Mapcode(mapcodePrecision0 = mapcodePrecision0,
      mapcodePrecision1 = mapcodePrecision1,
      mapcodePrecision2 = mapcodePrecision2,
      territory = territory)
  }

  /**
   * This method provides a shortcut to checking if a mapcode string is formatted properly or not at all.
   *
   * @param mapcode Mapcode string.
   * @return True if the mapcode format, the syntax, is correct. This does not mean the mapcode is actually a valid
   *         mapcode representing a location on Earth.
   */
  def isValidMapcodeFormat(mapcode: String): Boolean = {
    getMapcodeFormatType(mapcode) ne MapcodeFormatType.MAPCODE_TYPE_INVALID
  }

  /**
   * This method return the mapcode type, given a mapcode string. If the mapcode string has an invalid
   * format, {@link MapcodeFormatType#MAPCODE_TYPE_INVALID} is returned. If another value is returned,
   * the precision of the mapcode is given.
   *
   * Note that this method only checks the syntactic validity of the mapcode, the string format. It does not
   * check if the mapcode is really a valid mapcode representing a position on Earth.
   *
   * @param mapcode Mapcode string.
   * @return Type of mapcode format, or { @link MapcodeFormatType#MAPCODE_TYPE_INVALID} if not valid.
   */
  def getMapcodeFormatType(mapcode: String): MapcodeFormatType.MapcodeFormatType = {
    val decodedMapcode: String = convertToAscii(mapcode)
    if (!PATTERN_MAPCODE_FORMAT.pattern.matcher(decodedMapcode).matches) {
      MapcodeFormatType.MAPCODE_TYPE_INVALID
    } else {
      val matcherMapcodePrecision = PATTERN_MAPCODE_PRECISION.pattern.matcher(decodedMapcode)
      if (!matcherMapcodePrecision.find) {
        MapcodeFormatType.MAPCODE_TYPE_PRECISION_0
      } else {
        val length = matcherMapcodePrecision.end - matcherMapcodePrecision.start
        assert((2 <= length) && (length <= 3))
        if (length == 2) MapcodeFormatType.MAPCODE_TYPE_PRECISION_1
        else MapcodeFormatType.MAPCODE_TYPE_PRECISION_2
      }
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
  object MapcodeFormatType extends Enumeration {
    type MapcodeFormatType = Value
    val MAPCODE_TYPE_INVALID, MAPCODE_TYPE_PRECISION_0, MAPCODE_TYPE_PRECISION_1, MAPCODE_TYPE_PRECISION_2 = Value
  }

}

