package models

case class Forum(_id: String,
                 name: String,
                 description: Option[String],
                 category: String,
                 position: Int,
                 readonly: Boolean,
                 forumPermissions: Option[Map[String, AccessRule]],
                 threadPermissions: Option[Map[String, AccessRule]]) extends BaseModel[Forum] {

  lazy val forumPermissionMap = forumPermissions.getOrElse(Map.empty)

  lazy val threadPermissionMap = threadPermissions.getOrElse(Map.empty)

  override def withId(_id: String) = copy(_id = _id)

}
