name := "atlas"
version := "0.1.0"

lazy val root = (project in file("."))

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.10",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.0.10" % "test",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)
