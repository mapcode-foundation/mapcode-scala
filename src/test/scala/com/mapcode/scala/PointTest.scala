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

import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSuite, Matchers}

import scala.util.Random

class PointTest extends FunSuite with Matchers with GeneratorDrivenPropertyChecks {

  val DELTA = 0.000001

  test("testDegreesLatToMeters") {
    Point.degreesLatToMeters(0) should be(0d)
    Point.degreesLatToMeters(0.5) should be(Point.METERS_PER_DEGREE_LAT / 2d)
    Point.degreesLatToMeters(1) should be(Point.METERS_PER_DEGREE_LAT)
    Point.degreesLatToMeters(90) should be(Point.METERS_PER_DEGREE_LAT * 90)
    Point.degreesLatToMeters(-90) should be(-Point.METERS_PER_DEGREE_LAT * 90)
  }

  test("testDegreesLonToMeters") {
    Point.degreesLonToMetersAtLat(0, 0) should be(0d)
    Point.degreesLonToMetersAtLat(0.5, 0) should be(Point.METERS_PER_DEGREE_LON_EQUATOR / 2d)
    Point.degreesLonToMetersAtLat(1, 0) should be(Point.METERS_PER_DEGREE_LON_EQUATOR)
    Point.degreesLonToMetersAtLat(180, 0) should be(Point.METERS_PER_DEGREE_LON_EQUATOR * 180)
    Point.degreesLonToMetersAtLat(-180, 0) should be(-Point.METERS_PER_DEGREE_LON_EQUATOR * 180)

    Point.degreesLonToMetersAtLat(1, 60) should be(Point.METERS_PER_DEGREE_LON_EQUATOR / 2.0 +- DELTA)
    Point.degreesLonToMetersAtLat(1, -60) should be(Point.METERS_PER_DEGREE_LON_EQUATOR / 2.0 +- DELTA)
  }

  test("testMetersToDegreesLon") {
    Point.metersToDegreesLonAtLat(0, 0) should equal(0d)
    Point.metersToDegreesLonAtLat(Point.METERS_PER_DEGREE_LON_EQUATOR / 2, 0) should be(0.5d)
    Point.metersToDegreesLonAtLat(Point.METERS_PER_DEGREE_LON_EQUATOR, 0) should be(1d)
    Point.metersToDegreesLonAtLat(Point.METERS_PER_DEGREE_LON_EQUATOR * 180, 0) should be(180)
    Point.metersToDegreesLonAtLat(Point.METERS_PER_DEGREE_LON_EQUATOR * -180, 0) should be(-180)
    Point.metersToDegreesLonAtLat(Point.METERS_PER_DEGREE_LON_EQUATOR, 60) should be(2.0 +- DELTA)
    Point.metersToDegreesLonAtLat(Point.METERS_PER_DEGREE_LON_EQUATOR, -60) should be(2.0 +- DELTA)
  }

  test("testDistanceInMeters") {
    Point.METERS_PER_DEGREE_LAT - Point.fromMicroDeg(-500000, 0).distanceInMeters(Point.fromMicroDeg(500000, 0)) should be(0d +- DELTA)
    Point.METERS_PER_DEGREE_LAT - Point.fromMicroDeg(80000000, 0).distanceInMeters(Point.fromMicroDeg(81000000, 0)) should be(0d +- DELTA)
    (Point.METERS_PER_DEGREE_LAT * 2) - Point.fromMicroDeg(59000000, 0).distanceInMeters(Point.fromMicroDeg(61000000, 0)) should be(0d +- DELTA)
    (Point.METERS_PER_DEGREE_LON_EQUATOR - Point.fromMicroDeg(0, -500000).distanceInMeters(Point.fromMicroDeg(0, 500000))) should be(0d +- DELTA)
    Point.METERS_PER_DEGREE_LON_EQUATOR - Point.fromMicroDeg(0, 80000000).distanceInMeters(Point.fromMicroDeg(0, 81000000)) should be(0d +- DELTA)
    (Point.METERS_PER_DEGREE_LON_EQUATOR / 2.0) - Point.fromMicroDeg(60000000, 80000000).distanceInMeters(Point.fromMicroDeg(60000000, 81000000)) should be(0d +- DELTA)
    ((Point.METERS_PER_DEGREE_LON_EQUATOR * 2) - Point.fromMicroDeg(0, -1000000).distanceInMeters(Point.fromMicroDeg(0, 1000000))) should be(0d +- DELTA)
    Point.fromDeg(0.0, 180.0).distanceInMeters(Point.fromDeg(0.0, -179.999977)) should be < 10d
    Point.fromDeg(0.0, -179.999977).distanceInMeters(Point.fromDeg(0.0, 180.0)) should be < 10d
  }

  test("normalize") {
    forAll {
      (lat: Double, lon: Double) =>
        val point = Point(lat, lon)
        val normalized = point.normalize
        normalized.latDeg should be >= Point.LAT_DEG_MIN
        normalized.latDeg should be <= Point.LAT_DEG_MAX
        normalized.lonDeg should be >= Point.LON_DEG_MIN
        normalized.lonDeg should be <= Point.LON_DEG_MAX
    }

    Point.undefined.normalize should equal(Point.undefined)
  }

  test("distanceBetween") {
    Point(90, 0) unitDistanceBetween Point(-90, 0) should be(math.Pi)
    Point(90, 0) unitDistanceBetween Point(0, 0) should be(math.Pi / 2)
  }

  test("fromUniformlyDistributedRandomPoints") {

    // something of a flawed heuristic to see how well our random point distribution
    // is doing. I think actually the algorithm we are using clusters points
    // at the poles; see http://www.jasondavies.com/maps/random-points/ for
    // some possibly better algorithms

    // some utility functions
    def average(p: Seq[Double]) = p.sum / p.size
    def sq(x: Double) = x * x

    val n = 1000
    // generated n random points
    val points = (1 to n).map(_ => Point.fromUniformlyDistributedRandomPoints(Random))
    // for each point, find the distance to its nearest point
    val closestPoints = points.map(point => points.filterNot(_ == point).map(_ unitDistanceBetween point).min)
    // compute the area of the circle at each point with a radius of the average closest distance
    val coverage = math.Pi * sq(average(closestPoints)) * n
    val areaOfUnitSphere = 4 * math.Pi
    // this coverage of those circles should be within 30% of the area of the unit sphere
    coverage should be(areaOfUnitSphere +- areaOfUnitSphere * 0.3)
  }
}


