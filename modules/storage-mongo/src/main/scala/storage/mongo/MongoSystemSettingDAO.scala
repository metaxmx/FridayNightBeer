package storage.mongo

import javax.inject.{Inject, Singleton}

import models.SystemSetting
import play.api.cache.CacheApi
import play.modules.reactivemongo.{ReactiveMongoApi, ReactiveMongoComponents}
import reactivemongo.bson.{BSONDocumentReader, BSONDocumentWriter}
import storage.SystemSettingDAO

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

@Singleton
class MongoSystemSettingDAO @Inject()(cacheApi: CacheApi, val reactiveMongoApi: ReactiveMongoApi)
  extends MongoGenericDAO[SystemSetting](cacheApi, "systemsettings") with ReactiveMongoComponents with BSONContext[SystemSetting] with SystemSettingDAO {

  implicit val bsonWriter = implicitly[BSONDocumentWriter[SystemSetting]]

  implicit val bsonReader = implicitly[BSONDocumentReader[SystemSetting]]

  override def getSetting[A](key: String, defaultValue: A)(implicit ttag: TypeTag[A], ctag: ClassTag[A]): Future[A] = {
    (this ?? key).toFuture flatMap {
      case None =>
        changeSetting(key, defaultValue)
      case Some(setting) =>
        Future.successful(serializeFromString(setting.value))
    }
  }

  def changeSetting[A](key: String, value: A)(implicit ttag: TypeTag[A], ctag: ClassTag[A]): Future[A] = {
    val insertFuture = this <<! SystemSetting(key, serializeToString(value))
    insertFuture map { _ => value } // Ignore result to skip de-serializing again and just return inserted value
  }

  private[this] def serializeToString[A](value: A)(implicit ttag: TypeTag[A], ctag: ClassTag[A]): String = {
    if (typeOf[A] =:= typeOf[String]) value.asInstanceOf[String]
    else if (typeOf[A] =:= typeOf[Boolean]) String.valueOf(value.asInstanceOf[Boolean])
    else if (typeOf[A] =:= typeOf[Int]) String.valueOf(value.asInstanceOf[Int])
    else if (typeOf[A] =:= typeOf[Long]) String.valueOf(value.asInstanceOf[Long])
    else if (typeOf[A] =:= typeOf[Float]) String.valueOf(value.asInstanceOf[Float])
    else if (typeOf[A] =:= typeOf[Double]) String.valueOf(value.asInstanceOf[Double])
    else throw new IllegalArgumentException("Cannot serialize type " + ttag)
  }

  private[this] def serializeFromString[A](value: String)(implicit ttag: TypeTag[A], ctag: ClassTag[A]): A = {
    if (typeOf[A] =:= typeOf[String]) value.asInstanceOf[A]
    else if (typeOf[A] =:= typeOf[Boolean]) value.toBoolean.asInstanceOf[A]
    else if (typeOf[A] =:= typeOf[Int]) value.toInt.asInstanceOf[A]
    else if (typeOf[A] =:= typeOf[Long]) value.toLong.asInstanceOf[A]
    else if (typeOf[A] =:= typeOf[Float]) value.toFloat.asInstanceOf[A]
    else if (typeOf[A] =:= typeOf[Double]) value.toDouble.asInstanceOf[A]
    else throw new IllegalArgumentException("Cannot deserialize type " + ttag)
  }

}
