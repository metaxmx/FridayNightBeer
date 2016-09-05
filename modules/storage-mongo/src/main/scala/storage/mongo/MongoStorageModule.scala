package storage.mongo

import com.google.inject.AbstractModule
import storage._

/**
  * Play Module to register mongo storage.
  */
class MongoStorageModule extends AbstractModule {

  override def configure(): Unit = {
    binder bind classOf[ForumCategoryDAO] to classOf[MongoForumCategoryDAO]
    binder bind classOf[ForumDAO] to classOf[MongoForumDAO]
    binder bind classOf[GroupDAO] to classOf[MongoGroupDAO]
    binder bind classOf[PostDAO] to classOf[MongoPostDAO]
    binder bind classOf[SessionDAO] to classOf[MongoSessionDAO]
    binder bind classOf[ThreadDAO] to classOf[MongoThreadDAO]
    binder bind classOf[UserDAO] to classOf[MongoUserDAO]
    binder bind classOf[PermissionDAO] to classOf[MongoPermissionDAO]
    binder bind classOf[SystemSettingDAO] to classOf[MongoSystemSettingDAO]
  }

}
