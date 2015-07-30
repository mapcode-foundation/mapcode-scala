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

import com.mapcode.{Point => JPoint}

import scala.util.Random

/**
 * This class defines a class for lat/lon points.
 *
 * Note that this class behaves like a {{{case class Point(latDeg: Double, lonDeg: Double)}}}.
 */
class Point private (delegate: JPoint) {

  def latDeg: Double =
    delegate.getLatDeg

  def lonDeg: Double =
    delegate.getLonDeg

  override def equals(other: Any) =
    delegate.equals(other)

  override def hashCode =
    delegate.hashCode

  override def toString: String =
    delegate.toString
}

object Point {

  // Latitude and longitude ranges.
  val LON_DEG_MIN: Double = JPoint.LON_DEG_MIN
  val LON_DEG_MAX: Double = JPoint.LON_DEG_MAX
  val LAT_DEG_MIN: Double = JPoint.LAT_DEG_MIN
  val LAT_DEG_MAX: Double = JPoint.LAT_DEG_MAX

  val LON_MICRODEG_MIN: Int = JPoint.LON_MICRODEG_MIN
  val LON_MICRODEG_MAX: Int = JPoint.LON_MICRODEG_MAX
  val LAT_MICRODEG_MIN: Int = JPoint.LAT_MICRODEG_MIN
  val LAT_MICRODEG_MAX: Int = JPoint.LAT_MICRODEG_MAX

  val MICRODEG_TO_DEG_FACTOR: Double = JPoint.MICRODEG_TO_DEG_FACTOR

  // Radius of Earth.
  val EARTH_RADIUS_X_METERS: Double = JPoint.EARTH_RADIUS_X_METERS
  val EARTH_RADIUS_Y_METERS: Double = JPoint.EARTH_RADIUS_Y_METERS

  // Circumference of Earth.
  val EARTH_CIRCUMFERENCE_X: Double = JPoint.EARTH_CIRCUMFERENCE_X
  val EARTH_CIRCUMFERENCE_Y: Double = JPoint.EARTH_CIRCUMFERENCE_Y

  // Meters per degree latitude is fixed. For longitude: use factor * cos(midpoint of two degree latitudes).
  val METERS_PER_DEGREE_LAT: Double = JPoint.METERS_PER_DEGREE_LAT
  val METERS_PER_DEGREE_LON_EQUATOR: Double = JPoint.METERS_PER_DEGREE_LON_EQUATOR

  /**
   * Create a point from lat/lon in degrees.
   *
   * @param latDeg Longitude in degrees.
   * @param lonDeg Latitude in degrees.
   * @return A defined point.
   */
  def fromDeg(latDeg: Double, lonDeg: Double): Point =
    JPoint.fromDeg(latDeg, lonDeg)

  /**
   * Create a random point, uniformly distributed over the surface of the Earth.
   *
   * @param randomGenerator Random generator used to create a point.
   * @return Random point with uniform distribution over the sphere.
   */
  def fromUniformlyDistributedRandomPoints(randomGenerator: Random): Point =
    JPoint.fromUniformlyDistributedRandomPoints(randomGenerator.self)

  /**
   * Calculate the distance between two points. This algorithm does not take the curvature of the Earth into
   * account, so it only works for small distance up to, say 200 km, and not too close to the poles.
   *
   * @param p1 Point 1.
   * @param p2 Point 2.
   * @return Straight distance between p1 and p2. Only accurate for small distances up to 200 km.
   */
  def distanceInMeters(p1: Point, p2: Point): Double =
    JPoint.distanceInMeters(p1, p1)

  def degreesLatToMeters(latDegrees: Double): Double =
    JPoint.degreesLatToMeters(latDegrees)

  def degreesLonToMetersAtLat(lonDegrees: Double, lat: Double): Double =
    JPoint.degreesLonToMetersAtLat(lonDegrees, lat)

  def metersToDegreesLonAtLat(eastMeters: Double, lat: Double): Double =
    JPoint.metersToDegreesLonAtLat(eastMeters, lat)

  def apply(latDeg: Double, lonDeg: Double) =
    new Point(JPoint.fromDeg(latDeg, lonDeg))

  def unapply(point: Point): Option[(Double, Double)] =
    Some((point.latDeg, point.lonDeg))

  implicit def fromJava(point: JPoint): Point =
    new Point(point)

  implicit def toJava(point: Point): JPoint =
    JPoint.fromDeg(point.latDeg, point.lonDeg)
}

