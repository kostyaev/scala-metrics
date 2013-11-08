import ch.qos.logback.classic.LoggerContext
import com.codahale.metrics.logback.InstrumentedAppender
import com.readytalk.metrics.StatsDReporter
import java.util.concurrent.TimeUnit
import org.slf4j.{LoggerFactory, Logger}

object Metrics extends App {

  /** The application wide metrics registry. */
  val metricRegistry = new com.codahale.metrics.MetricRegistry()


  /** StatsD Report */
  StatsDReporter.forRegistry(metricRegistry)
    .build("localhost", 8125)
    .start(10, TimeUnit.SECONDS);

  /** Records the rate of logged events */
  val factory :LoggerContext  = LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]
  val rootLogger = factory.getLogger(Logger.ROOT_LOGGER_NAME);
  val metrics: InstrumentedAppender = new InstrumentedAppender(metricRegistry);
  metrics.setContext(rootLogger.getLoggerContext());
  metrics.start();
  rootLogger.addAppender(metrics);

  val timer = new Timer()

  print("Running ...")
  val s = timer.run()
  println(" done.")

}