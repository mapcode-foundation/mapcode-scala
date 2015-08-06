name := "mapcode-scala"
organization := "com.mapcode"
version := "2.0.0-SNAPSHOT"

scalaVersion := "2.11.7"
scalacOptions ++= Seq("-feature")

// easier to work with snapshots during testing
resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
  "com.mapcode" % "mapcode" % "2.0.2" withSources(),
  "com.google.code.findbugs" % "jsr305" % "1.3.+",
  "org.slf4j" % "slf4j-api" % "1.7.2" % "test",
  "org.slf4j" % "slf4j-log4j12" % "1.7.2" % "test",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "org.scalacheck" %% "scalacheck" % "1.11.6" % "test"
)

import ScoverageSbtPlugin.ScoverageKeys._
coverageExcludedPackages := "com\\.mapcode\\.scala\\.Territory"
coverageHighlighting := true
