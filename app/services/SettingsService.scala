package services

import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.BSONDocument
import play.modules.reactivemongo.ReactiveMongoPlugin.db
import play.api.Play.current
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import javax.inject.Singleton
import models.FnbSetting
import dtos.SettingsDTO
import play.api.Logger
import exceptions.QueryException
import reactivemongo.api.ReadPreference

@Singleton
class SettingsService {

  val CACHE_INTERVAL_SETTINGS = 24 * 60 * 60

  val cacheSettings = new TypedSingletonCache[Seq[FnbSetting]]("db.settings", CACHE_INTERVAL_SETTINGS)

  val cacheSettingsDTO = new TypedSingletonCache[SettingsDTO]("dto.settings", CACHE_INTERVAL_SETTINGS)

  def settingsCollection = db.collection[BSONCollection](FnbSetting.collectionName)

  def findSettingsFromDb: Future[Seq[FnbSetting]] = {
    Logger.info(s"Fetching Settings from database")
    settingsCollection.find(BSONDocument()).cursor[FnbSetting](ReadPreference.Primary).collect[Seq]() recover {
      case exc => {
        Logger.error("Error loading settings", exc)
        throw new QueryException("Error loading settings", exc)
      }
    }
  }

  def findSettings: Future[Seq[FnbSetting]] = cacheSettings.getOrElseAsyncDef(findSettingsFromDb)

  def findSettingsDto: Future[SettingsDTO] = cacheSettingsDTO.getOrElseAsyncDef(findSettings map { SettingsDTO.fromSettings(_) })

}