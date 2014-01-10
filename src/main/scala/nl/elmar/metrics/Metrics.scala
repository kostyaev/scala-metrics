package nl.elmar.metrics

import com.codahale.metrics.health.HealthCheck
import java.util.concurrent.TimeUnit
import java.util.Random
import com.codahale.metrics.{JmxReporter, ConsoleReporter, MetricRegistry}
import nl.grons.metrics.scala.Timer

import com.codahale.metrics.Counter

object Metrics extends App {

  /** Registry. Application wide */
  val metrics = new MetricRegistry()

  // Reporter

  /**
   * The {{{ConsoleReporter}}} will report the metrics to {{{StdOut}}}
   *
   * It is build into the {{{core}}} module
   */
  val consoleReporter: ConsoleReporter = ConsoleReporter.forRegistry(metrics)
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
  val jmxReporter: JmxReporter = JmxReporter.forRegistry(metrics).build()
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
  val evictions: Counter = metrics.counter(MetricRegistry.name(classOf[HealthCheckDemo], "cache-evictions"))

  /** produce some data */
  def run() {

    new TimerExample().longRunningInstrumentedMethod()

    evictions.inc()
    Thread.sleep(1500)
    evictions.inc(3)
    Thread.sleep(1500)
    evictions.dec()
    Thread.sleep(1500)
    evictions.dec(2)
    Thread.sleep(1500)
  }

  // run the app
  run()

}

trait Instrumented extends nl.grons.metrics.scala.InstrumentedBuilder {
  val metricRegistry = Metrics.metrics
}

class TimerExample extends Instrumented {

  /**
   * A timer measures the rate how often a piece of code was called but also the distribution of the duration
   */
  private[this] val running: Timer = metrics.timer("calculation-duration")

  /** some long running calculation */
  def longRunningInstrumentedMethod() = running.time {
    Thread.sleep(5000)
  }
}

class HealthCheckDemo extends HealthCheck {

  override def check(): HealthCheck.Result =
    if (new Random().nextInt() % 2 == 0) {
      HealthCheck.Result.unhealthy("EVEN Random")
    } else {
      HealthCheck.Result.healthy("ODD Random")
    }
}
