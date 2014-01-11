name := "typesafe-config-example"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.10.3"

organization := "org.littlewings"

scalacOptions ++= Seq("-Xlint", "-deprecation", "-unchecked")

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.0.2",
  "org.scalatest" %% "scalatest" % "2.0" % "test"
)
