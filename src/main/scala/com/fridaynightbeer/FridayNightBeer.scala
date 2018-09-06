package com.fridaynightbeer

import java.nio.file.Files

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import com.fridaynightbeer.util.Logging

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
  * FridayNightBeer main App implementation
  */
object FridayNightBeer extends Logging {

  val settings: Settings = Settings.load()

  implicit lazy val system: ActorSystem = {
    logger.debug("Starting Actor System fnb")
    ActorSystem("fnb")
  }
  implicit lazy val materializer: Materializer = ActorMaterializer()

  lazy val httpService: HttpService = HttpService()

  def init(): Unit = {
    logger.info(s"Starting FridayNightBeer version ${settings.Version.version}")
    prepareDataDir()
    registerShutDownHandlers()
    runHttpService()
  }

  private def prepareDataDir(): Unit = {
    if (logger.isDebugEnabled) {
      logger.debug(s"Using data directory ${settings.Storage.dataDir}")
    }
    if (!Files.exists(settings.Storage.dataPath)) {
      logger.warn(s"Data directory ${settings.Storage.dataDir} not existing - trying to create it")
      Files.createDirectories(settings.Storage.dataPath)
    }
  }

  private def runHttpService(): Unit = {
    implicit val ec: ExecutionContext = system.dispatcher
    httpService.runHttpService() onComplete {
      case Failure(e) =>
        logger.error("Could not start HTTP Service", e)
        shutdownHook.remove()
        shutdown()
      case Success(binding) =>
        logger.info(s"Http Service started, awaiting connections at ${settings.Http.fullUrl}")
    }
  }

  lazy val shutdownHook = sys.addShutdownHook(shutdown())

  private def registerShutDownHandlers(): Unit = {
    system // init system
    shutdownHook // init shutdown hook
  }

  def shutdown(): Unit = {
    logger.info("Terminating...")
    Await.ready(system.terminate(), 30.seconds)
    logger.info("Friday Night Beer terminated")
  }

}
