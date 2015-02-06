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

private[scala] object CheckArgs {
  def checkRange[T <% Ordered[T]](param: => String, value: T, min: T, max: T): Unit =
    require(value >= min && value <= max, s"Parameter $param should be in range [$min, $max], but is: $value")

  def checkNonnull(param: String, obj: AnyRef): Unit =
    require(obj != null, s"Parameter $param should not be null")

  def verify[T](value: T, msg: => String)(f: T => Boolean): T = {
    assert(f(value), msg)
    value
  }
}
