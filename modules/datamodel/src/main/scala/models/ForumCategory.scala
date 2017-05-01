package models

case class ForumCategory(_id: String,
                         name: String,
                         position: Int,
                         forumPermissions: Option[Map[String, AccessRule]],
                         threadPermissions: Option[Map[String, AccessRule]]) extends BaseModel[ForumCategory] {

  lazy val forumPermissionMap: Map[String, AccessRule] = forumPermissions.getOrElse(Map.empty)

  lazy val threadPermissionMap: Map[String, AccessRule] = threadPermissions.getOrElse(Map.empty)

  override def withId(_id: String): ForumCategory = copy(_id = _id)

}
