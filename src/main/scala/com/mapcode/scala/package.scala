package com.mapcode

package object scala {

  import com.mapcode.scala.{Mapcode => SMapcode, Point => SPoint}
  import com.mapcode.{Mapcode => JMapcode, Point => JPoint}

  implicit class JMapcodeConverter(val mapcode: JMapcode) extends AnyVal {
    def asScala: SMapcode = new Mapcode(mapcode)
  }

  implicit class MapcodeConverter(val mapcode: SMapcode) extends AnyVal {
    def asJava: JMapcode = new JMapcode(mapcode.code, mapcode.territory)
  }

  implicit class JPointConverter(val point: JPoint) extends AnyVal {
    def asScala: SPoint = new SPoint(point)
  }

  implicit class PointConverter(val point: SPoint) extends AnyVal {
    def asJava: JPoint = JPoint.fromDeg(point.latDeg, point.lonDeg)
  }

}
