trait Instrumented extends nl.grons.metrics.scala.InstrumentedBuilder {
  val metricRegistry = Metrics.metricRegistry
}