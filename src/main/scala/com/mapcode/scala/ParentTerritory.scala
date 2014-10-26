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
 * This class defines "parent territories" for territories that have multiple territory codes. In other words,
 * Countries that have states or provinces that are fairly first-class, e.g. USA.
 */
object ParentTerritory {
  // this is a Seq instead of a Set because Set is invariant and we need covariance
  // to break out TerritoryOperations from Territories.
  // not sure a big deal since it's a tiny list.
  // Note that we have a unit test that confirms this matches what is in
  // the Territory object. This is really just for convenience; the same data
  // can be accessed through Territory.territories.
  lazy val values = Seq(IND, AUS, BRA, USA, MEX, CAN, RUS, CHN, ATA)
  val IND = Territory.IND
  val AUS = Territory.AUS
  val BRA = Territory.BRA
  val USA = Territory.USA
  val MEX = Territory.MEX
  val CAN = Territory.CAN
  val RUS = Territory.RUS
  val CHN = Territory.CHN
  val ATA = Territory.ATA
}
