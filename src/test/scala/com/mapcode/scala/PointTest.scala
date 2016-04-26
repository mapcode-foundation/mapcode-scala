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

import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{FunSuite, Matchers}

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
    Point.fromDeg(0.0, 180.0).distanceInMeters(Point.fromDeg(0.0, -179.999977)) should be < 10d
    Point.fromDeg(0.0, -179.999977).distanceInMeters(Point.fromDeg(0.0, 180.0)) should be < 10d
  }

  test("normalize") {
    forAll {
      (lat: Double, lon: Double) =>
        val point = Point(lat, lon)
        point.latDeg should be >= Point.LAT_DEG_MIN
        point.latDeg should be <= Point.LAT_DEG_MAX
        point.lonDeg should be >= Point.LON_DEG_MIN
        point.lonDeg should be <= Point.LON_DEG_MAX
    }
  }
}


