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
import com.mapcode.{Mapcode => JMapcode, UnknownPrecisionFormatException}

import scala.util.matching.Regex

/**
 * This class defines a single mapcode encoding result, including the alphanumeric code and the
 * territory definition.
 *
 * On terminology, mapcode territory and mapcode code:
 *
 * In written form, a mapcode is defined as an alphanumeric code, optionally preceded by a
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
 * Note that this class is functionally equivalent to case class Mapcode(mapcode: String, territory: Territory).
 * {{{
 *   val mapcode = Mapcode("49.4V", Territory.USA)
 *
 *   mapcode match {
 *     case Mapcode(code, territory) =>
 *   }
 * }}}
 */
class Mapcode private (val delegate: JMapcode) {

  /**
   * Get the Mapcode string (without territory information) with standard precision.
   * The returned mapcode does not include the '-' separator and additional digits.
   *
   * The returned precision is approximately 5 meters. The precision is defined as the maximum distance to the
   * (latitude, longitude) pair that encoded to this mapcode, which means the mapcode defines an area of
   * approximately 10 x 10 meters (100 m2).
   *
   * @param precision Precision specifier. Range: [0, 8].
   * @param alphabet Alphabet.
   * @return Mapcode string.
   * @throws IllegalArgumentException Thrown if precision is out of range (must be in [0, 8]).
   */
  def code(precision: Int, alphabet: Alphabet): String =
    delegate.getCode(precision, alphabet)

  def code(alphabet: Alphabet): String =
    delegate.getCode(alphabet)

  def code(precision: Int): String =
    delegate.getCode(precision)

  def code =
    delegate.getCode

  /**
   * Return the full international mapcode, including the full name of the territory and the mapcode code itself.
   * The format of the string is:
   * full-territory-name cde
   *
   * Example:
   * Netherlands 49.4V           (regular code)
   * Netherlands 49.4V-K2        (high precision code)
   *
   * @param precision Precision specifier. Range: [0, 8].
   * @param alphabet  Alphabet.
   * @return Full international mapcode.
   * @throws IllegalArgumentException Thrown if precision is out of range (must be in [0, 8]).
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
   * @param precision Precision specifier. Range: [0, 8].
   * @param alphabet  Alphabet.
   * @return Short-hand international mapcode.
   * @throws IllegalArgumentException Thrown if precision is out of range (must be in [0, 8]).
   */
  def codeWithTerritory(precision: Int, alphabet: Alphabet): String =
    delegate.getCodeWithTerritory(precision, alphabet)

  def codeWithTerritory(precision: Int): String =
    delegate.getCodeWithTerritory(precision)

  def codeWithTerritory(alphabet: Alphabet): String =
    delegate.getCodeWithTerritory(alphabet)

  def codeWithTerritory: String =
    delegate.getCodeWithTerritory

  def territory: Territory =
    delegate.getTerritory

  override def equals(other: Any) =
    delegate.equals(other)

  override def hashCode =
    delegate.hashCode

  override def toString =
    delegate.toString
}

object Mapcode {

  /**
   * This regular expression is used to check mapcode format strings.
   * They've been made public to allow others to use the correct regular expressions as well.
   */
  val REGEX_MAPCODE: Regex = JMapcode.REGEX_MAPCODE.r

  /**
   * This method return the mapcode type, given a mapcode string. If the mapcode string has an invalid
   * format, an exception is thrown.
   *
   * Note that this method only checks the syntactic validity of the mapcode, the string format. It does not
   * check if the mapcode is really a valid mapcode representing a position on Earth.
   *
   * @param mapcode Mapcode (optionally with a territory).
   * @return Type of mapcode precision format (0-8).
   * @throws UnknownPrecisionFormatException If precision format is incorrect.
   */
  def precisionFormat(mapcode: String): Integer =
    JMapcode.getPrecisionFormat(mapcode)

  /**
   * This method provides a shortcut to checking if a mapcode string is formatted properly or not at all.
   *
   * @param mapcode Mapcode (optionally with a territory).
   * @return True if the mapcode format, the syntax, is correct. This does not mean the mapcode code is
   *         actually a valid  mapcode representing a location on Earth.
   * @throws IllegalArgumentException If mapcode is null.
   */
  def isValidMapcodeFormat(mapcode: String): Boolean =
    JMapcode.isValidMapcodeFormat(mapcode)

  /**
   * Returns whether the mapcode contains territory information or not.
   *
   * @param mapcode Mapcode string, optionally with territory information.
   * @return True if mapcode contains territory information.
   * @throws IllegalArgumentException If mapcode has incorrect syntax.
   */
   def containsTerritory(mapcode: String): Boolean =
     JMapcode.containsTerritory(mapcode)

  /**
   * Get a safe maximum for the distance between a decoded mapcode and its original
   * location used for encoding the mapcode. The actual accuracy (resolution) of mapcodes is
   * better than this, but these are safe values to use under normal circumstances.
   *
   * Do not make any other assumptions on these numbers than that mapcodes are never more off
   * by this distance.
   *
   * @param precision Precision of mapcode.
   * @return Maximum offset in meters.
   */
  def safeMaxOffsetInMeters(precision: Int): Double =
    JMapcode.getSafeMaxOffsetInMeters(precision)

  /**
   * Public constructor for Mapcode.
   * @param code      Code of mapcode.
   * @param territory Territory.
   * @throws IllegalArgumentException Thrown if syntax not valid or if the mapcode string contains
   *                                  territory information.
   * @return Mapcode instance
   */
  def apply(code: String, territory: Territory): Mapcode =
    new JMapcode(code, territory)

  /**
   * Allows pattern matching for Mapcode instances as if it were a case class.
   * @param mapcode Mapcode to match
   * @return Code and territory fields of the mapcode.
   */
  def unapply(mapcode: Mapcode): Option[(String, Territory)] =
    Some((mapcode.code, mapcode.territory))

  implicit def fromJava(mapcode: JMapcode): Mapcode =
    new Mapcode(mapcode)

  implicit def toJava(mapcode: Mapcode): JMapcode =
    mapcode.delegate
}

