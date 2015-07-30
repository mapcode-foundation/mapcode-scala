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

import com.mapcode.{MapcodeCodec => JMapcodeCodec, _}
import _root_.scala.collection.JavaConversions._

object MapcodeCodec {

  /**
   * Encode a lat/lon pair to a mapcode with territory information. This produces a non-empty list of mapcode,
   * with at the very least 1 mapcodes for the lat/lon, which is the "International" mapcode.
   *
   * The returned result list will always contain at least 1 mapcode, because every lat/lon pair can be encoded.
   *
   * The list is ordered in such a way that the last result is the international code. However, you cannot assume
   * that the first result is the shortest mapcode. If you want to use the shortest mapcode, use
   * [[ #encodeToShortest(double, double, Territory) ]].
   *
   * The international code can be obtained from the list by using: "results.get(results.size() - 1)", or
   * you can use [[ #encodeToInternational(double, double)]], which is faster.
   *
   * @param latDeg Latitude, accepted range: -90..90.
   * @param lonDeg Longitude, accepted range: -180..180.
   * @return Non-empty, ordered list of mapcode information records, see [[Mapcode]].
   * @throws IllegalArgumentException Thrown if latitude or longitude are out of range.
   */
  def encode(latDeg: Double, lonDeg: Double): Seq[Mapcode] =
    JMapcodeCodec.encode(latDeg, lonDeg).map(Mapcode.fromJava)

  def encode(point: Point): Seq[Mapcode] =
    JMapcodeCodec.encode(point).map(Mapcode.fromJava)

  /**
   * Encode a lat/lon pair to a mapcode with territory information, for a specific territory. This produces a
   * potentially empty list of mapcodes (empty if the lat/lon does not fall within the territory for mapcodes).
   *
   * The returned result list will always contain at least 1 mapcode, because every lat/lon pair can be encoded.
   *
   * The list is ordered in such a way that the last result is the international code. However, you cannot assume
   * that the first result is the shortest mapcode. If you want to use the shortest mapcode, use
   * [[ #encodeToShortest(double, double, Territory)]].
   *
   * @param latDeg              Latitude, accepted range: -90..90 (limited to this range if outside).
   * @param lonDeg              Longitude, accepted range: -180..180 (wrapped to this range if outside).
   * @param restrictToTerritory Try to encode only within this territory, see { @link Territory}. May be null.
   * @return List of mapcode information records, see { @link Mapcode}. This list is empty if no
   *                                                          Mapcode can be generated for this territory matching the lat/lon.
   * @throws IllegalArgumentException Thrown if latitude or longitude are out of range.
   */
  def encode(latDeg: Double, lonDeg: Double, restrictToTerritory: Territory): Seq[Mapcode] =
    JMapcodeCodec.encode(latDeg, lonDeg, restrictToTerritory).map(Mapcode.fromJava)

  def encode(point: Point, restrictToTerritory: Territory): Seq[Mapcode] =
    JMapcodeCodec.encode(point, restrictToTerritory).map(Mapcode.fromJava)

  /**
   * Encode a lat/lon pair to its shortest mapcode with territory information.
   *
   * @param latDeg              Latitude, accepted range: -90..90.
   * @param lonDeg              Longitude, accepted range: -180..180.
   * @param restrictToTerritory Try to encode only within this territory, see { @link Territory}. Cannot be null.
   * @return Shortest mapcode, see { @link Mapcode}.
   * @throws IllegalArgumentException Thrown if latitude or longitude are out of range.
   * @throws UnknownMapcodeException  Thrown if no mapcode was found for the lat/lon matching the territory.
   */
  def encodeToShortest(latDeg: Double, lonDeg: Double, restrictToTerritory: Territory): Mapcode =
    JMapcodeCodec.encodeToShortest(latDeg, lonDeg, restrictToTerritory)

  def encodeToShortest(point: Point, restrictToTerritory: Territory): Mapcode =
    JMapcodeCodec.encodeToShortest(point, restrictToTerritory)

  /**
   * Encode a lat/lon pair to its unambiguous, international mapcode.
   *
   * @param latDeg Latitude, accepted range: -90..90.
   * @param lonDeg Longitude, accepted range: -180..180.
   * @return International unambiguous mapcode (always exists), see { @link Mapcode}.
   * @throws IllegalArgumentException Thrown if latitude or longitude are out of range.
   */
  def encodeToInternational(latDeg: Double, lonDeg: Double): Mapcode =
    JMapcodeCodec.encodeToInternational(latDeg, lonDeg)

  def encodeToInternational(point: Point): Mapcode =
    JMapcodeCodec.encodeToInternational(point)

  /**
   * ------------------------------------------------------------------------------------------
   * Decoding mapcodes back to latitude, longitude.
   * ------------------------------------------------------------------------------------------
   */
  /**
   * Decode a mapcode to a Point. The decoding process may fail for local mapcodes,
   * because no territory context is supplied (world-wide).
   *
   * The accepted format is:
   * {mapcode}
   * {territory-code} {mapcode}
   *
   * @param mapcode Mapcode.
   * @return Point corresponding to mapcode.
   * @throws UnknownMapcodeException         Thrown if the mapcode has the correct syntax,
   *                                         but cannot be decoded into a point.
   * @throws UnknownPrecisionFormatException Thrown if the precision format is incorrect.
   * @throws IllegalArgumentException        Thrown if arguments are null, or if the syntax of the mapcode is incorrect.
   */
  def decode(mapcode: String): Point =
    JMapcodeCodec.decode(mapcode)

  /**
   * Decode a mapcode to a Point. A reference territory is supplied for disambiguation (only used if applicable).
   *
   * The accepted format is:
   * {mapcode}
   * {territory-code} {mapcode}
   *
   * Note that if a territory-code is supplied in the string, it takes preferences over the parameter.
   *
   * @param mapcode                 Mapcode.
   * @param defaultTerritoryContext Default territory context for disambiguation purposes. May be null.
   * @return Point corresponding to mapcode. Latitude range: -90..90, longitude range: -180..180.
   * @throws UnknownMapcodeException         Thrown if the mapcode has the right syntax, but cannot be decoded into a point.
   * @throws UnknownPrecisionFormatException Thrown if the precision format is incorrect.
   * @throws IllegalArgumentException        Thrown if arguments are null, or if the syntax of the mapcode is incorrect.
   */
  def decode(mapcode: String, defaultTerritoryContext: Territory): Point =
    JMapcodeCodec.decode(mapcode, defaultTerritoryContext)
}




