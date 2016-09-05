package storage

import models.SystemSetting

import scala.concurrent.Future
import scala.reflect.ClassTag
import scala.reflect.runtime.universe.TypeTag

trait SystemSettingDAO  extends GenericDAO[SystemSetting] {

  def getSetting[A](key: String, defaultValue: A)(implicit ttag: TypeTag[A], ctag: ClassTag[A]): Future[A]

  def changeSetting[A](key: String, value: A)(implicit ttag: TypeTag[A], ctag: ClassTag[A]): Future[A]

}
