name := "ficus-example"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.10.3"

organization := "org.littlewings"

scalacOptions ++= Seq("-Xlint", "-deprecation", "-unchecked")

resolvers += "Sonatype OSS Releases" at "http://oss.sonatype.org/content/repositories/releases/"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.0.2",
  "net.ceedubs" %% "ficus" % "1.0.0",
  "org.scalatest" %% "scalatest" % "2.0" % "test"
)
