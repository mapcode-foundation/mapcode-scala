name := "mapcode-scala"
organization := "com.mapcode"
version := "2.2.3"

scalaVersion := "2.11.7"
crossScalaVersions := Seq("2.11.7", "2.10.4")
scalacOptions ++= Seq("-feature")

// easier to work with snapshots during testing
resolvers += Resolver.mavenLocal

libraryDependencies ++= Seq(
  "com.mapcode" % "mapcode" % "2.2.3" withSources(),
  "com.google.code.findbugs" % "jsr305" % "1.3.+",
  "org.slf4j" % "slf4j-api" % "1.7.2" % "test",
  "org.slf4j" % "slf4j-log4j12" % "1.7.2" % "test",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "org.scalacheck" %% "scalacheck" % "1.11.6" % "test"
)

licenses := Seq("The Apache Software Licence, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))
homepage := Some(url("https://github.com/mapcode-foundation/mapcode-scala"))
pomIncludeRepository := { _ => false }
publishMavenStyle := true
publishArtifact in Test := false
credentials += Credentials(Path.userHome / ".sbt" / "0.13" / "sonatype.sbt")
pomExtra := <scm>
  <connection>scm:git:git@github.com:mapcode-foundation/mapcode-scala.git</connection>
  <url>https://github.com/mapcode-foundation/mapcode-scala</url>
</scm>
  <developers>
    <developer>
      <id>ebowman</id>
      <name>Eric Bowman</name>
      <url>https://github.com/ebowman</url>
    </developer>
    <developer>
      <id>rditerwich</id>
      <name>Ruud Diterwich</name>
      <url>https://github.com/rditerwich</url>
    </developer>
    <developer>
      <id>rijnb</id>
      <name>Rijn Buve</name>
      <url>https://github.com/rijnb</url>
    </developer>
  </developers>

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}
