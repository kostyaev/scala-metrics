package nl.elmar.metrics

import com.codahale.metrics.health.HealthCheck
import com.codahale.metrics._
import java.util.concurrent.TimeUnit
import java.util.Random

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

  /**
   * A timer measures the rate how often a piece of code was called but also the distribution of the duration
   */
  val request: Timer = metrics.timer(MetricRegistry.name(classOf[ArithmeticDemoOperation], "calculation-duration"))

  /** produce some data */
  def run() {
    val ctx: Timer.Context = request.time()
    new ArithmeticDemoOperation().calculateSomeMagic()
    ctx.stop()

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

class ArithmeticDemoOperation {

  /** some long running calculation */
  def calculateSomeMagic() {
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
