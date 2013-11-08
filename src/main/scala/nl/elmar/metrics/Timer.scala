import java.util.concurrent.TimeUnit

class Timer extends Instrumented {
  private[this] val loading = metrics.timer("loading")

  val WaitingTime = TimeUnit.SECONDS.toMillis(5)

  def run() = loading.time {
    Thread.sleep(WaitingTime)
  }
}