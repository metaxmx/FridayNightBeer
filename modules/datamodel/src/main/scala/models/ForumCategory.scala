package models

case class ForumCategory(_id: String,
                         name: String,
                         position: Int,
                         forumPermissions: Option[Map[String, AccessRule]]) extends BaseModel[ForumCategory] {

  lazy val forumPermissionMap = forumPermissions.getOrElse(Map.empty)

  override def withId(_id: String) = copy(_id = _id)

}
