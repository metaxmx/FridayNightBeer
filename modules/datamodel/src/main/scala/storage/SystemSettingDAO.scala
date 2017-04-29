package storage

import models.SystemSetting

import scala.concurrent.Future
import scala.reflect.ClassTag
import scala.reflect.runtime.universe.TypeTag

/**
  * DAO for system settings.
  */
trait SystemSettingDAO  extends GenericDAO[SystemSetting] {

  /**
    * Get system setting. If setting is missing in the database, insert the default value into the database.
    * @param key key to query
    * @param defaultValue default value to return, when the system setting is not found in the storage
    * @tparam A value type
    * @return future of the setting value
    */
  def getSetting[A : TypeTag : ClassTag](key: String, defaultValue: A): Future[A]

  /**
    * Change system setting.
    * @param key key to store to
    * @param value setting value
    * @tparam A value type
    * @return future of the setting value
    */
  def changeSetting[A : TypeTag : ClassTag](key: String, value: A): Future[A]

}
