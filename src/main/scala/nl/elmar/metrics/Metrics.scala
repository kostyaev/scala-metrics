package nl.elmar.metrics

import com.codahale.metrics.health.HealthCheck
import java.util.concurrent.TimeUnit
import java.util.Random
import com.codahale.metrics.{JmxReporter, ConsoleReporter}
import nl.grons.metrics.scala.{InstrumentedBuilder, CheckedBuilder, Timer, Counter, Histogram}


object Metrics extends App {

  /** The application wide metrics registry. */
  val metricRegistry = new com.codahale.metrics.MetricRegistry()
  /** The application wide health check registry. */
  val healthCheckRegistry = new com.codahale.metrics.health.HealthCheckRegistry()

  /**
   * The {{{ConsoleReporter}}} will report the metrics to {{{StdOut}}}
   *
   * It is build into the {{{core}}} module
   */
  ConsoleReporter.forRegistry(metricRegistry)
    .convertRatesTo(TimeUnit.SECONDS)
    .convertDurationsTo(TimeUnit.MILLISECONDS)
    .build().start(10, TimeUnit.SECONDS)

  /**
   * The {{JmxReporter}} will make the metrics available to JConsole or VisualVM (if you install the MBeans plugin).
   *
   * VisualVM can even graph the data for that property.
   */
  JmxReporter.forRegistry(metricRegistry).build().start()

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

  /** produce some data */
  def run() {

    new TimerExample().longRunningInstrumentedMethod()

    new RandomNumberGaugeExample().fetchingRandomNumber()

    // TODO print healthCheckResults
    new HealthCheckExample().check()

    val histogramExample = new SearchResultsHistogramExample()
    histogramExample.search("foo")
    histogramExample.search("bar")

    val cache = new CacheCounterExample()
    cache.put("foo", "bar")
    Thread.sleep(1500)
    cache.put("foo", "bar")
    cache.put("foo", "bar")
    cache.put("foo", "bar")
    Thread.sleep(1500)
    cache.remove("foo")
    Thread.sleep(1500)
    cache.remove("foo")
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

  def fetchingRandomNumber(): Int = {
    metrics.gauge("random-number") {
      new Random().nextInt() % 1000
    }.value
  }
}

/**
 * A counter is a specific type of {{{Gauge}}} for {{{AtomicLong}}} instances. For instance you want to measure the
 * number of cache evictions
 */
class CacheCounterExample() extends Instrumented {

  private[this] val counter: Counter = metrics.counter("cache-evictions")

  def put(key: String, value: String) = {
    counter.inc()
  }

  def remove(key: String) = {
    counter.dec()
  }

}

/**
 * Histogram metrics allow you to measure not just easy things like the min, mean, max, and standard deviation of
 * values, but also quantiles like the median or 95th percentile.
 *
 * Traditionally, the way the median (or any other quantile) is calculated is to take the entire data set, sort it,
 * and take the value in the middle (or 1% from the end, for the 99th percentile). This of course does not work for
 * high-throughput, low-latency services.
 *
 * The solution is a technique is called _reservoir sampling_.
 *
 * Metrics provides a number of different Reservoir implementations, each of which is useful.
 */
class SearchResultsHistogramExample() extends Instrumented {

  private[this] val resultCounts: Histogram = metrics.histogram("result-counts")

  def search(key: String) {
    // search for stuff
    val numberOfResults = new Random().nextInt() % 50
    resultCounts += numberOfResults
  }

}

/**
 * A health check for your service or application
 */
class HealthCheckExample extends Checked {

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
