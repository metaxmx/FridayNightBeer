import java.net.InetSocketAddress
import java.util.concurrent.atomic.AtomicReference

import play.sbt.PlayRunHook
import sbt._

object Webpack {

  val operatingSystem = sys.props.getOrElse("os.name", "unknown")
  val npmCmd = if(operatingSystem contains "Windows") "cmd /c npm" else "npm"
  val webpackCmd = if(operatingSystem contains "Windows") "cmd /c webpack" else "webpack"

  def apply(base: File): PlayRunHook = {
    object WebpackHook extends PlayRunHook {
      val proc = new AtomicReference[Option[Process]](None)

      override def beforeStarted() = {
        s"$npmCmd install".!
        proc.set(Some(Process(s"$webpackCmd", base).run()))
      }

      override def afterStarted(address: InetSocketAddress) = {
        proc.set(Some(Process(s"$webpackCmd --watch", base).run()))
      }

      override def afterStopped() = {
        proc.get().foreach(_.destroy())
        proc.set(None)
      }
    }

    WebpackHook
  }
}