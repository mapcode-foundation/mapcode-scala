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

import com.mapcode.scala.CheckArgs.verify

/**
 * This class is the external Scala interface for encoding and decoding mapcodes.
 */
object MapcodeCodec {
  /**
   * Encode a lat/lon pair to a mapcode with territory information. This produces a non-empty list of mapcode,
   * with at least 1 mapcode for the lat/lon, which is the "International" mapcode.
   *
   * The returned result list will always contain at least 1 mapcode, because every lat/lon pair can be encoded.
   *
   * The list is ordered in such a way that the first result contains the shortest mapcode (which is usually a
   * local mapcode). The last result contains the "International" or world-wide mapcode, which is always
   * unambiguous, even when used without a territory specification.
   *
   * The international code can be obtained from call like `encode(lat, lon).last`.
   *
   * @param latDeg Latitude, accepted range: -90..90.
   * @param lonDeg Longitude, accepted range: -180..180.
   * @return Non-empty, ordered list of mapcode information records, see { @link Mapcode}.
   */
  def encode(latDeg: Double, lonDeg: Double): Seq[Mapcode] = {
    val results = Encoder.encode(latDeg, lonDeg, None, isRecursive = false, limitToOneResult = false, allowWorld = true)
    verify(results, s"Every coordinate should resolve to something: ($latDeg, $lonDeg)")(_.size >= 1)
  }

  /**
   * Encode a lat/lon pair to its shortest mapcode without territory information. For a valid lat/lon pair, this will
   * always yield a mapcode.
   *
   * @param latDeg Latitude in degrees, accepted range: -90..90.
   * @param lonDeg Longitude in degrees, accepted range: -180..180.
   * @return Shortest mapcode (always exists), see { @link Mapcode}.
   * @throws IllegalArgumentException Thrown if latitude or longitude are out of range.
   */
  def encodeToShortest(latDeg: Double, lonDeg: Double): Mapcode = {
    val results = Encoder.encode(latDeg, lonDeg, None, isRecursive = false, limitToOneResult = true, allowWorld = true)
    verify(results, s"Should result in at least 1 result: ($latDeg, $lonDeg)")(_.size >= 1).head
  }

  /**
   * Encode a lat/lon pair to its shortest mapcode with territory information.
   *
   * @param latDeg              Latitude, accepted range: -90..90.
   * @param lonDeg              Longitude, accepted range: -180..180.
   * @param restrictToTerritory Try to encode only within this territory, see { @link com.mapcode.Territory}. Cannot
   *                            be null.
   * @return Shortest mapcode, see { @link Mapcode}.
   * @throws IllegalArgumentException Thrown if latitude or longitude are out of range.
   * @throws UnknownMapcodeException  Thrown if no mapcode was found for the lat/lon matching the territory.
   */
  def encodeToShortest(latDeg: Double, lonDeg: Double, restrictToTerritory: Territory.Territory): Mapcode = {
    require(restrictToTerritory != null, "Null territory not allowed")
    val results = Encoder.encode(latDeg, lonDeg, Some(restrictToTerritory),
      isRecursive = false, limitToOneResult = true, allowWorld = false)
    if (results.isEmpty) {
      throw new UnknownMapcodeException(s"No Mapcode for lat=$latDeg, lon=$lonDeg, territory=$restrictToTerritory")
    }
    verify(results, s"Should result in exactly 1 result: ($latDeg, $lonDeg): $results")(_.size == 1).head
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
    val results = encode(latDeg, lonDeg, Territory.AAA)
    verify(results, s"Should result in at least 1 result: ($latDeg, $lonDeg) => $results")(_.size >= 1)
    results.last
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
   *                            be null.
   * @return List of mapcode information records, see { @link Mapcode}. This list is empty if no
   *         Mapcode can be generated for this territory matching the lat/lon.
   * @throws IllegalArgumentException Thrown if latitude or longitude are out of range.
   */
  def encode(latDeg: Double, lonDeg: Double, restrictToTerritory: Territory.Territory): Seq[Mapcode] = {
    require(restrictToTerritory != null, "Null territory not allowed")
    Encoder.encode(latDeg, lonDeg, Some(restrictToTerritory), isRecursive = false, limitToOneResult = false, allowWorld = false)
  }

  /**
   * Decode a mapcode to a Point. The decoding process may fail for local mapcodes,
   * because no territory context is supplied (world-wide).
   *
   * The accepted format is:<br><br>
   * `<mapcode><br>
   * <territory-code> <mapcode>`
   *
   * @param mapcode Mapcode.
   * @return Point corresponding to mapcode.
   * @throws UnknownMapcodeException  Thrown if the mapcode has the correct syntax, but cannot be decoded into a point.
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
      throw new IllegalArgumentException(
        s"$mapcode is not a correctly formatted mapcode; must match regex: ${Mapcode.FormatRx}")
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
      s"$mapcode is not a correctly formatted mapcode; must match regex: ${Mapcode.FormatRx}")
    val point: Point = Decoder.decode(mapcodeTrimmed, territoryContext)
    if (!point.isDefined) {
      throw new UnknownMapcodeException("Unknown Mapcode: " + mapcodeTrimmed + ", territoryContext=" + territoryContext)
    }
    assert((Point.LAT_DEG_MIN <= point.latDeg) && (point.latDeg <= Point.LAT_DEG_MAX), point.latDeg)
    assert((Point.LON_DEG_MIN <= point.lonDeg) && (point.lonDeg <= Point.LON_DEG_MAX), point.lonDeg)
    point
  }
}


