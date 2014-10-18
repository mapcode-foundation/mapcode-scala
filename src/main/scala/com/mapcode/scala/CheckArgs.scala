package com.mapcode.scala

private [scala] object CheckArgs {
  private[mapcode] def checkRange(param: String, value: Double, min: Double, max: Double): Unit =
    require(value >= min && value <= max, s"Parameter $param should be in range [$min, $max], but is: $value")

  private[mapcode] def checkNonnull(param: String, obj: AnyRef): Unit =
    require(obj != null, s"Parameter $param should not be null")
}
