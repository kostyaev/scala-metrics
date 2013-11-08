organization := "com.acme"

name := "scala-metrics"

version := "1.0"

scalaVersion := "2.10.3"

resolvers += "Metrics StatsD Releases" at "http://dl.bintray.com/readytalk/maven"

libraryDependencies ++= Seq(
	"nl.grons" %% "metrics-scala" % "3.0.3",
    "com.codahale.metrics" % "metrics-graphite" % "3.0.1",
    "com.codahale.metrics" % "metrics-logback" % "3.0.1",
	"com.readytalk" % "metrics3-statsd" % "3.0.0-RC1",
	"org.slf4j" % "slf4j-api" % "1.7.5",
	"ch.qos.logback" % "logback-core" % "1.0.13",
	"ch.qos.logback" % "logback-classic" % "1.0.13"
)
