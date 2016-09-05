package util

import java.io.File
import javax.inject.{Inject, Singleton}

import play.api.Configuration

/**
  * Application Settings.
  * @param config play config
  */
@Singleton
class AppSettings @Inject() (implicit config: Configuration) {

  private[this] val fnbConfig = config.getConfig("fnb")

  val dataPath: String = fnbConfig flatMap (_ getString "datadir") getOrElse "appdata"

  val dataDir: File = new File(dataPath)

  private[this] def validateDataDir(): Unit = {
    if (!dataDir.isDirectory) throw config.reportError("fnb.datadir", "Datadir '" + dataDir.getPath + "' does not exist or is no directory")
    if (!dataDir.canWrite) throw config.reportError("fnb.datadir", "Datadir '\" + dataDir.getPath + \"' is not writable")
  }

  validateDataDir()

}
