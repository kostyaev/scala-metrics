package nl.elmar.metrics

import com.codahale.metrics.health.HealthCheck
import java.util.concurrent.TimeUnit
import java.util.Random
import com.codahale.metrics.{JmxReporter, ConsoleReporter, MetricRegistry}
import nl.grons.metrics.scala.{InstrumentedBuilder, CheckedBuilder, Timer}

import com.codahale.metrics.Counter

object Metrics extends App {

  /** The application wide metrics registry. */
  val metricRegistry = new com.codahale.metrics.MetricRegistry()
  /** The application wide health check registry. */
  val healthCheckRegistry = new com.codahale.metrics.health.HealthCheckRegistry();


  // Reporter

  /**
   * The {{{ConsoleReporter}}} will report the metrics to {{{StdOut}}}
   *
   * It is build into the {{{core}}} module
   */
  val consoleReporter: ConsoleReporter = ConsoleReporter.forRegistry(metricRegistry)
    .convertRatesTo(TimeUnit.SECONDS)
    .convertDurationsTo(TimeUnit.MILLISECONDS)
    .build()

  // should expose values every 10 seconds
  consoleReporter.start(10, TimeUnit.SECONDS)

  /**
   * The {{JmxReporter}} will make the metrics available to JConsole or VisualVM (if you install the MBeans plugin).
   *
   * VisualVM can even graph the data for that property.
   */
  val jmxReporter: JmxReporter = JmxReporter.forRegistry(metricRegistry).build()
  jmxReporter.start()

  //
  // Metrics
  //
  // Each metric has a unique name, which is a simple dotted name, like {{{com.example.Queue.size}}}.
  //
  // This flexibility allows you to encode a wide variety of context directly into a metric’s name
  //
  // The full name will be resolved using the class. As all the classes in this example live in {{{nl.elmar.metrics}}}
  // they will all start with {{{nl.elmar.metrics}}}
  //

  /**
   * A counter is a specific type of {{{Gauge}}} for {{{AtomicLong}}} instances. For instance you want to measure the
   * number of cache evictions
   */
  val evictions: Counter = metricRegistry.counter(MetricRegistry.name(classOf[HealthCheckDemo], "cache-evictions"))

  /** produce some data */
  def run() {

    new TimerExample().longRunningInstrumentedMethod()

    new RandomNumberGaugeExample().fetchingRandomNumber()

    evictions.inc()
    Thread.sleep(1500)
    evictions.inc(3)
    Thread.sleep(1500)
    evictions.dec()
    Thread.sleep(1500)
    evictions.dec(4)
    Thread.sleep(1500)
  }

  // run the app
  run()

  while(true){}

}

/**
 * Trait that passes {{{metricsRegistry}}} around
 */
trait Instrumented extends InstrumentedBuilder {
  val metricRegistry = Metrics.metricRegistry
}

/**
 * Trait that passes {{{healthCheckRegistry}}} around
 */
trait Checked extends CheckedBuilder {
  val registry = Metrics.healthCheckRegistry
}

/**
 * A timer measures the rate how often a piece of code was called but also the distribution of the duration
 */
class TimerExample extends Instrumented {

  private[this] val running: Timer = metrics.timer("calculation-duration")

  /** some long running calculation */
  def longRunningInstrumentedMethod() = running.time {
    Thread.sleep(5000)
  }
}

/**
 * A gauge is the most simple metrics. It just returns a single value.
 */
class RandomNumberGaugeExample() extends Instrumented {

  def fetchingRandomNumber() = {


    metrics.gauge("random-number") {
      new
          Random().nextInt() % 1000
    }
  }
}

/**
 * A health check for your service or application
 */
class HealthCheckDemo extends Checked {

  def check(): HealthCheck = {
    healthCheck("database") {
      if (new Random().nextInt() % 2 == 0) {
        HealthCheck.Result.unhealthy("EVEN Random")
      } else {
        HealthCheck.Result.healthy("ODD Random")
      }
    }
  }


}
