
name := "mapcode-scala"

organization := "com.mapcode"

version := "1.40.3-SNAPSHOT"

scalaVersion := "2.11.2"

scalacOptions ++= Seq("-feature")

// easier to work with snapshots during testing
resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.2",
  "org.slf4j" % "slf4j-log4j12" % "1.7.2" % "test",
  "com.mapcode" % "mapcode" % "1.40.3-SNAPSHOT" % "test" changing,
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "org.scalacheck" %% "scalacheck" % "1.11.6" % "test"
)

