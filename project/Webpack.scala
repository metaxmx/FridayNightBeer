import java.net.InetSocketAddress
import play.sbt.PlayRunHook
import sbt._

object Webpack {

  val operatingSystem = sys.props.getOrElse("os.name", "unknown")
  val npmCmd = if(operatingSystem contains "Windows") "cmd /c npm" else "npm"
  val webpackCmd = if(operatingSystem contains "Windows") "cmd /c webpack" else "webpack"

  def apply(base: File): PlayRunHook = {
    object WebpackHook extends PlayRunHook {
      var process: Option[Process] = None

      override def beforeStarted() = {
        s"$npmCmd install".!
        process = Option(
          Process(s"$webpackCmd", base).run()
        )
      }

      override def afterStarted(address: InetSocketAddress) = {
        process = Option(
          Process(s"$webpackCmd --watch", base).run()
        )
      }

      override def afterStopped() = {
        process.foreach(_.destroy())
        process = None
      }
    }

    WebpackHook
  }
}