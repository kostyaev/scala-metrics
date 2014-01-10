organization := "com.acme"

name := "scala-metrics"

version := "1.0"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  "nl.grons" %% "metrics-scala" % "3.0.4",
  // Reporting via Graphite
  "com.codahale.metrics" % "metrics-graphite" % "3.0.1",
  // Reporting via JMX wants logback to be configured
  "org.slf4j" % "slf4j-api" % "1.7.5",
  "ch.qos.logback" % "logback-core" % "1.0.13",
  "ch.qos.logback" % "logback-classic" % "1.0.13"
)
