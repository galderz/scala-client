name := "infinispan-hotrod-scala"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.2"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature")

libraryDependencies ++= Seq(
  "io.netty" % "netty-all" % "4.0.23.Final",
  "org.jboss.marshalling" % "jboss-marshalling" % "1.4.4.Final",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test"
)
