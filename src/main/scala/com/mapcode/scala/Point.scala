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

import com.mapcode.scala.CheckArgs.checkNonnull

import scala.util.Random

/**
 * This class defines a class for lat/lon points.
 */
private[scala] object Point {
  /**
   * Create an undefined points. No latitude or longitude can be obtained from it.
   * Only within the mapcode implementation points can be undefined, so this methods is package private.
   *
   * @return Undefined points.
   */
  private[mapcode] lazy val undefined: Point = Point(Double.NaN, Double.NaN)
  val LON_DEG_MIN: Double = -180.0
  val LON_DEG_MAX: Double = 180.0
  val LAT_DEG_MIN: Double = -90.0
  val LAT_DEG_MAX: Double = 90.0
  val MICRODEG_TO_DEG_FACTOR: Double = 1000000.0
  val EARTH_RADIUS_X_METERS: Double = 6378137.0
  val EARTH_RADIUS_Y_METERS: Double = 6356752.3
  val LON_MICRODEG_MIN: Int = degToMicroDeg(LON_DEG_MIN)
  val LON_MICRODEG_MAX: Int = degToMicroDeg(LON_DEG_MAX)
  val LAT_MICRODEG_MIN: Int = degToMicroDeg(LAT_DEG_MIN)
  val LAT_MICRODEG_MAX: Int = degToMicroDeg(LAT_DEG_MAX)
  val EARTH_CIRCUMFERENCE_X: Double = EARTH_RADIUS_X_METERS * 2.0 * math.Pi
  val EARTH_CIRCUMFERENCE_Y: Double = EARTH_RADIUS_Y_METERS * 2.0 * math.Pi
  val METERS_PER_DEGREE_LAT: Double = EARTH_CIRCUMFERENCE_Y / 360.0
  val METERS_PER_DEGREE_LON_EQUATOR: Double = EARTH_CIRCUMFERENCE_X / 360.0

  /**
   * Create a point from lat/lon in degrees.
   *
   * @param latDeg Longitude in degrees.
   * @param lonDeg Latitude in degrees.
   * @return A defined point.
   */
  def fromDeg(latDeg: Double, lonDeg: Double): Point = new Point(latDeg, lonDeg)

  /**
   * Create a random point, uniformly distributed over the surface of the Earth.
   *
   * @param randomGenerator Random generator used to create a point.
   * @return Random point with uniform distribution over the sphere.
   */
  def fromUniformlyDistributedRandomPoints(randomGenerator: Random): Point = {
    checkNonnull("randomGenerator", randomGenerator)
    val unitRand1 = randomGenerator.nextDouble()
    val unitRand2 = randomGenerator.nextDouble()
    val theta0 = (2.0 * math.Pi) * unitRand1
    val theta1 = math.acos(1.0 - (2.0 * unitRand2))
    val x = math.sin(theta0) * math.sin(theta1)
    val y = math.cos(theta0) * math.sin(theta1)
    val z = math.cos(theta1)
    val latRad = math.asin(z)
    val lonRad = math.atan2(y, x)
    val lat = latRad.toDegrees
    val lon = lonRad.toDegrees
    fromMicroDeg(degToMicroDeg(lat), degToMicroDeg(lon))
  }

  /**
   * Create a point from lat/lon in micro-degrees (i.e. degrees * 1,000,000).
   *
   * @param latMicroDeg Longitude in microdegrees.
   * @param lonMicroDeg Latitude in microdegrees.
   * @return A defined point.
   */
  def fromMicroDeg(latMicroDeg: Int, lonMicroDeg: Int): Point =
    new Point(microDegToDeg(latMicroDeg), microDegToDeg(lonMicroDeg))

  def microDegToDeg(microDeg: Int): Double = microDeg.asInstanceOf[Double] / MICRODEG_TO_DEG_FACTOR

  def degToMicroDeg(deg: Double): Int = math.round(deg * MICRODEG_TO_DEG_FACTOR).asInstanceOf[Int]

  def degreesLatToMeters(latDegrees: Double): Double = latDegrees * METERS_PER_DEGREE_LAT

  def degreesLonToMetersAtLat(lonDegrees: Double, lat: Double): Double =
    lonDegrees * METERS_PER_DEGREE_LON_EQUATOR * math.cos(math.toRadians(lat))

  def metersToDegreesLonAtLat(eastMeters: Double, lat: Double): Double =
    (eastMeters / METERS_PER_DEGREE_LON_EQUATOR) / math.cos(math.toRadians(lat))
}

case class Point(latDeg: Double, lonDeg: Double) {

  import com.mapcode.scala.Point._

  /**
   * @return Latitude in microdegrees. No range is enforced.
   */
  def latMicroDeg: Int = degToMicroDeg(latDeg)

  /**
   * @return Longitude in microdegrees. No range is enforced.
   */
  def lonMicroDeg: Int = degToMicroDeg(lonDeg)

  def normalize: Point = {
    if (isDefined) {
      def clamp(min: Double, max: Double, value: Double) = math.max(math.min(max, value), min)
      Point(clamp(LAT_DEG_MIN, LAT_DEG_MAX, latDeg), clamp(LON_DEG_MIN, LON_DEG_MAX, lonDeg))
    } else this
  }

  /**
   * Return whether the point is defined or not.
   * Only within the mapcode implementation points can be undefined, so this methods is package private.
   *
   * @return True if defined. If false, no lat/lon is available.
   */
  private[mapcode] def isDefined: Boolean = this != undefined

  /** Distance between two points on the unit sphere. */
  def unitDistanceBetween(p: Point): Double = {
    // adapted from http://www.movable-type.co.uk/scripts/latlong.html
    val φ1 = this.latDeg.toRadians
    val φ2 = p.latDeg.toRadians
    val Δφ = (p.latDeg - this.latDeg).toRadians
    val Δλ = (p.lonDeg - this.lonDeg).toRadians

    val a = math.sin(Δφ / 2) * math.sin(Δφ / 2) + math.cos(φ1) * math.cos(φ2) * math.sin(Δλ / 2) * math.sin(Δλ / 2)
    2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
  }

  /**
   * Calculate the distance between two points. This algorithm does not take the curvature of the Earth into
   * account, so it only works for small distance up to, say 200 km, and not too close to the poles.
   *
   * @param p2 Point 2.
   * @return Straight distance between p1 and p2. Only accurate for small distances up to 200 km.
   */
  def distanceInMeters(p2: Point): Double = {
    val (from, to) =
      if (this.lonDeg <= p2.lonDeg) (this, p2)
      else (p2, this)

    val avgLat = from.latDeg + ((to.latDeg - from.latDeg) / 2.0)
    val deltaLonDeg360 = math.abs(to.lonDeg - from.lonDeg)
    val deltaLonDeg = if (deltaLonDeg360 <= 180.0) deltaLonDeg360 else 360.0 - deltaLonDeg360
    val deltaLatDeg = math.abs(to.latDeg - from.latDeg)
    val deltaXMeters = degreesLonToMetersAtLat(deltaLonDeg, avgLat)
    val deltaYMeters = degreesLatToMeters(deltaLatDeg)
    math.sqrt((deltaXMeters * deltaXMeters) + (deltaYMeters * deltaYMeters))
  }
}

