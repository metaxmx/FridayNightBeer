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

  val siteTitle: String = fnbConfig flatMap (_ getString "sitetitle") getOrElse "Untitled Site"

  val dataPath: String = fnbConfig flatMap (_ getString "datadir") getOrElse "appdata"

  val dataDir: File = new File(dataPath)

  val defaultTheme: String = fnbConfig flatMap (_ getString "defaulttheme") getOrElse "default"

  val registerEnabled: Boolean = fnbConfig flatMap (_ getBoolean  "registerenabled") getOrElse false

  private[this] def validateDataDir(): Unit = {
    if (!dataDir.isDirectory) throw config.reportError("fnb.datadir", "Datadir does not exist or is no directory")
    if (!dataDir.canWrite) throw config.reportError("fnb.datadir", "Datadir is not writable")
  }

  validateDataDir()

}
