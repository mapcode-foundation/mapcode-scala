
name := "mapcode-scala"

organization := "com.mapcode"

version := "1.40.3-SNAPSHOT"

scalaVersion := "2.11.2"

scalacOptions ++= Seq("-feature")

libraryDependencies ++= Seq(
  "com.mapcode" % "mapcode" % "1.40.2",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "org.scalacheck" %% "scalacheck" % "1.11.6" % "test"
)


