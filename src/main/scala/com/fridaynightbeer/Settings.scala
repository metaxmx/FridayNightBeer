package com.fridaynightbeer

import java.nio.file.{Path, Paths}

import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.language.implicitConversions

/**
  * Settings object.
  * @param config underlying typesafe config
  */
case class Settings(config: Config) {

  import Settings.asFiniteDuration

  object Version {

    val major = config.getString("fnb.version.major")
    val minor = config.getString("fnb.version.minor")
    val patchLevel = config.getString("fnb.version.patchlevel")

    val version = s"$major.$minor.$patchLevel"

  }

  object Storage {

    val dataDir: String = config.getString("fnb.storage.datadir")
    val dataPath: Path = Paths.get(dataDir)

  }

  object Http {

    val interface: String = config.getString("fnb.http.interface")
    val port: Int = config.getInt("fnb.http.port")
    val domain: String = config.getString("fnb.http.domain")
    val fullUrl: String = config.getString("fnb.http.fullurl")

  }

  object Actors {

    val defaultTimeout: FiniteDuration  = config.getDuration("fnb.actors.default_timeout")

  }


}

object Settings {

  implicit def asFiniteDuration(d: java.time.Duration): FiniteDuration = Duration.fromNanos(d.toNanos)

  /**
    * Load default settings
    * @return Settings object
    */
  def load(): Settings = Settings {
    ConfigFactory.load()
  }

}
