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

import CheckArgs.{checkRange, checkNonnull}

/**
 * ----------------------------------------------------------------------------------------------
 * Mapcode public interface.
 * ----------------------------------------------------------------------------------------------
 *
 * This class is the external Scala interface for encoding and decoding mapcodes.
 */
object MapcodeCodec {
  /**
   * Encode a lat/lon pair to a mapcode with territory information. This produces a non-empty list of mapcode,
   * with at the very least 1 mapcodes for the lat/lon, which is the "International" mapcode.
   *
   * The returned result list will always contain at least 1 mapcode, because every lat/lon pair can be encoded.
   *
   * The list is ordered in such a way that the first result contains the shortest mapcode (which is usually a
   * local mapcode). The last result contains the "International" or world-wide mapcode, which is always
   * unambiguous, even when used without a territory specification.
   *
   * The international code can be obtained from the list by using: "results.get(results.size() - 1)".
   *
   * @param latDeg Latitude, accepted range: -90..90.
   * @param lonDeg Longitude, accepted range: -180..180.
   * @return Non-empty, ordered list of mapcode information records, see { @link Mapcode}.
   */
  def encode(latDeg: Double, lonDeg: Double): Seq[Mapcode] = {
    checkRange("latDeg", latDeg, Point.LAT_DEG_MIN, Point.LAT_DEG_MAX)
    checkRange("lonDeg", lonDeg, Point.LON_DEG_MIN, Point.LON_DEG_MAX)
    val results: Seq[Mapcode] = Encoder.encode(latDeg, lonDeg, None, isRecursive = false, limitToOneResult = false, allowWorld = true)
    assert(results.size >= 1)
    results
  }

  /**
   * Encode a lat/lon pair to a mapcode with territory information, for a specific territory. This produces a
   * potentially empty list of mapcodes (empty if the lat/lon does not fall within the territory for mapcodes).
   *
   * The returned result list will always contain at least 1 mapcode, because every lat/lon pair can be encoded.
   *
   * The list is ordered in such a way that the first result contains the shortest mapcode (which is usually a
   * local mapcode).
   *
   * @param latDeg              Latitude, accepted range: -90..90.
   * @param lonDeg              Longitude, accepted range: -180..180.
   * @param restrictToTerritory Try to encode only within this territory, see { @link com.mapcode.Territory}. Cannot
   *                                                                                  be null.
   * @return List of mapcode information records, see { @link Mapcode}. This list is empty if no
   *                                                          Mapcode can be generated for this territory matching the lat/lon.
   * @throws IllegalArgumentException Thrown if latitude or longitude are out of range.
   */
  def encode(latDeg: Double, lonDeg: Double, restrictToTerritory: Territory.Territory): Seq[Mapcode] = {
    checkRange("latDeg", latDeg, Point.LAT_DEG_MIN, Point.LAT_DEG_MAX)
    checkRange("lonDeg", lonDeg, Point.LON_DEG_MIN, Point.LON_DEG_MAX)
    checkNonnull("restrictToTerritory", restrictToTerritory)
    Encoder.encode(latDeg, lonDeg, Some(restrictToTerritory), isRecursive = false, limitToOneResult = false, allowWorld = false)
  }

  /**
   * Encode a lat/lon pair to its shortest mapcode without territory information. For a valid lat/lon pair, this will
   * always yield a mapcode.
   *
   * @param latDeg Latitude, accepted range: -90..90.
   * @param lonDeg Longitude, accepted range: -180..180.
   * @return Shortest mapcode (always exists), see { @link Mapcode}.
   * @throws IllegalArgumentException Thrown if latitude or longitude are out of range.
   */
  def encodeToShortest(latDeg: Double, lonDeg: Double): Mapcode = {
    checkRange("latDeg", latDeg, Point.LAT_DEG_MIN, Point.LAT_DEG_MAX)
    checkRange("lonDeg", lonDeg, Point.LON_DEG_MIN, Point.LON_DEG_MAX)
    val results  = Encoder.encode(latDeg, lonDeg, None, isRecursive = false, limitToOneResult = true, allowWorld = true)
    assert(results.size == 1)
    results.head
  }

  /**
   * Encode a lat/lon pair to its shortest mapcode with territory information.
   *
   * @param latDeg              Latitude, accepted range: -90..90.
   * @param lonDeg              Longitude, accepted range: -180..180.
   * @param restrictToTerritory Try to encode only within this territory, see { @link com.mapcode.Territory}. Cannot
   *                                                                                  be null.
   * @return Shortest mapcode, see { @link Mapcode}.
   * @throws IllegalArgumentException Thrown if latitude or longitude are out of range.
   * @throws UnknownMapcodeException  Thrown if no mapcode was found for the lat/lon matching the territory.
   */
  def encodeToShortest(latDeg: Double, lonDeg: Double, restrictToTerritory: Territory.Territory): Mapcode = {
    checkRange("latDeg", latDeg, Point.LAT_DEG_MIN, Point.LAT_DEG_MAX)
    checkRange("lonDeg", lonDeg, Point.LON_DEG_MIN, Point.LON_DEG_MAX)
    val results  = Encoder.encode(latDeg, lonDeg, Some(restrictToTerritory), isRecursive = false, limitToOneResult = true, allowWorld = false)
    if (results.isEmpty) {
      throw new UnknownMapcodeException("No Mapcode for lat=" + latDeg + ", lon=" + lonDeg + ", territory=" + restrictToTerritory)
    }
    assert(results.size == 1)
    results.head
  }

  /**
   * Encode a lat/lon pair to its unambiguous, international mapcode.
   *
   * @param latDeg Latitude, accepted range: -90..90.
   * @param lonDeg Longitude, accepted range: -180..180.
   * @return International unambiguous mapcode (always exists), see { @link Mapcode}.
   * @throws IllegalArgumentException Thrown if latitude or longitude are out of range.
   */
  def encodeToInternational(latDeg: Double, lonDeg: Double): Mapcode = {
    checkRange("latDeg", latDeg, Point.LAT_DEG_MIN, Point.LAT_DEG_MAX)
    checkRange("lonDeg", lonDeg, Point.LON_DEG_MIN, Point.LON_DEG_MAX)
    val results  = encode(latDeg, lonDeg, Territory.AAA)
    assert(results.size >= 1)
    results.last
  }

  /**
   * Decode a mapcode to a Point. The decoding process may fail for local mapcodes,
   * because no territory context is supplied (world-wide).
   *
   * The accepted format is:
   * <mapcode>
   * <territory-code> <mapcode>
   *
   * @param mapcode Mapcode.
   * @return Point corresponding to mapcode.
   * @throws UnknownMapcodeException  Thrown if the mapcode has the correct synaxt,
   *                                  but cannot be decoded into a point.
   * @throws IllegalArgumentException Thrown if arguments are null, or if the syntax of the mapcode is incorrect.
   */
  def decode(mapcode: String): Point = {
    var mapcodeTrimmed: String = mapcode.trim
    val space: Int = mapcodeTrimmed.indexOf(' ')
    var territory = Option.empty[Territory.Territory]
    if ((space > 0) && (mapcodeTrimmed.length > space)) {
      val territoryName: String = mapcodeTrimmed.substring(0, space).trim
      territory = Territory.fromString(territoryName)
      if (territory.isEmpty)
        throw new UnknownMapcodeException("Wrong territory code: " + territoryName)
      mapcodeTrimmed = mapcode.substring(space + 1).trim
    }
    else {
      territory = Some(Territory.AAA)
    }
    if (!Mapcode.isValidMapcodeFormat(mapcodeTrimmed)) {
      throw new IllegalArgumentException(mapcode + " is not a correctly formatted mapcode; " + "the regular expression for the mapcode syntax is: " + Mapcode.REGEX_MAPCODE_FORMAT)
    }
    decode(mapcodeTrimmed, territory.get)
  }

  /**
   * Decode a mapcode to a Point. A reference territory is supplied for disambiguation (only used if applicable).
   *
   * The accepted format is:
   * <mapcode>        (note that a territory code is not allowed here)
   *
   * @param mapcode          Mapcode.
   * @param territoryContext Territory for disambiguation purposes.
   * @return Point corresponding to mapcode. Latitude range: -90..90, longitude range: -180..180.
   * @throws UnknownMapcodeException  Thrown if the mapcode has the right syntax, but cannot be decoded into a point.
   * @throws IllegalArgumentException Thrown if arguments are null, or if the syntax of the mapcode is incorrect.
   */
  def decode(mapcode: String, territoryContext: Territory.Territory): Point = {
    val mapcodeTrimmed: String = mapcode.trim
    require(Mapcode.isValidMapcodeFormat(mapcodeTrimmed),
      mapcode + " is not a correctly formatted mapcode; " + "the regular expression for the mapcode syntax is: " + Mapcode.REGEX_MAPCODE_FORMAT)
    val point: Point = Decoder.decode(mapcodeTrimmed, territoryContext)
    if (!point.isDefined) {
      throw new UnknownMapcodeException("Unknown Mapcode: " + mapcodeTrimmed + ", territoryContext=" + territoryContext)
    }
    assert((Point.LAT_DEG_MIN <= point.latDeg) && (point.latDeg <= Point.LAT_DEG_MAX), point.latDeg)
    assert((Point.LON_DEG_MIN <= point.lonDeg) && (point.lonDeg <= Point.LON_DEG_MAX), point.lonDeg)
    point
  }
}


