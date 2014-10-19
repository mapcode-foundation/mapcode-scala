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
 * ----------------------------------------------------------------------------------------------
 * Package private implementation class. For internal use within the Mapcode implementation only.
 * ----------------------------------------------------------------------------------------------
 *
 * This class contains a class for dealing with ranges of comparable items.
 */
private[scala] case class Range[T <% Ordered[T]](min: T, max: T) {

  def containsRange(range: Range[T]): Boolean = min <= range.min && max >= range.max

  def intersects(range: Range[T]): Boolean =
    range.contains(min) || range.contains(max) || contains(range.max) || contains(range.min)

  def contains(value: T): Boolean = value >= min && value <= max

  /**
   * Used to constrain this range to a bunch of ranges. Note that constraint is not always possible;
   * when it is not possible, it is dropped. An empty sequence returned means no constraints were possible.
   *
   * @return versions of this range, constrained by each of the arguments, if possible.
   */
  def constrainAll(constrainingRanges: Seq[Range[T]]): Seq[Range[T]] = constrainingRanges.flatMap(constrain)

  /** @return a new version of this range, constrained by the argument, if possible */
  def constrain(constrainingRange: Range[T]): Option[Range[T]] = {
    val newMin = if (min < constrainingRange.min) constrainingRange.min else min
    val newMax = if (max > constrainingRange.max) constrainingRange.max else max
    if (newMax <= newMin) None
    else Some(Range(newMin, newMax))
  }
}

