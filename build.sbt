name := "mapcode-scala"
organization := "com.mapcode"
version := "1.40.3-SNAPSHOT"

scalaVersion := "2.11.7"
scalacOptions ++= Seq("-feature")

// easier to work with snapshots during testing
resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.2",
  "com.mapcode" % "mapcode" % "2.0.1",
  "com.google.code.findbugs" % "jsr305" % "1.3.9" % "provided",
  "org.slf4j" % "slf4j-log4j12" % "1.7.2" % "test",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "org.scalacheck" %% "scalacheck" % "1.12.4" % "test"
)

import scoverage.ScoverageKeys._
coverageExcludedPackages := "com\\.mapcode\\.scala\\.Territory"
coverageHighlighting := true

