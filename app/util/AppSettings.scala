package util

import java.io.File
import javax.inject.{Inject, Singleton}

import play.api.{Configuration, Logger}

/**
  * Application Settings.
  * @param config play config
  */
@Singleton
class AppSettings @Inject() (implicit val config: Configuration) {

  private def logger = Logger(getClass)

  private[this] val fnbConfig = config.getOptional[Configuration]("fnb")

  val dataPath: String = fnbConfig flatMap (_ getString "datadir") getOrElse "appdata"

  val dataDir: File = new File(dataPath)

  private[this] def validateDataDir(): Unit = {
    if (!dataDir.exists()) {
      if (!dataDir.mkdirs()) {
        throw config.reportError("fnb.datadir", s"Datadir '$dataPath' could not be created automatically")
      }
      logger.info(s"Successfully created application directory ${dataDir.getAbsolutePath}")
    }
    if (!dataDir.isDirectory) throw config.reportError("fnb.datadir", s"Datadir '$dataPath' does not exist or is no directory")
    if (!dataDir.canWrite) throw config.reportError("fnb.datadir", s"Datadir '$dataPath' is not writable")
    logger.info(s"Successfully initialized application directory ${dataDir.getAbsolutePath}")
  }

  validateDataDir()

}
