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
 * This class defines "parent territories" for territories that have multiple territory codes.
 */
object ParentTerritory extends Enumeration {
  import Territory.{Territory => Ter}

  final case class ParentTerritory(territory: Ter) extends Val

  val IND = ParentTerritory(Territory.IND)
  val AUS = ParentTerritory(Territory.AUS)
  val BRA = ParentTerritory(Territory.BRA)
  val USA = ParentTerritory(Territory.USA)
  val MEX = ParentTerritory(Territory.MEX)
  val CAN = ParentTerritory(Territory.CAN)
  val RUS = ParentTerritory(Territory.RUS)
  val CHN = ParentTerritory(Territory.CHN)
  val ATA = ParentTerritory(Territory.ATA)
}
