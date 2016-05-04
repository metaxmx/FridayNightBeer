package permissions

/**
  * Created by Christian Simon on 04.05.2016.
  */
object TopicPermissions extends PermissionType {

  sealed abstract class TopicPermission extends PermissionEnum

  case object Access extends TopicPermission

  val values = Seq(Access)

  lazy val valuesByName = values.map { value => value.name -> value }.toMap

  object TopicPermission {

    def apply(name: String): TopicPermission = valuesByName(name)

    def unapply(permission: TopicPermission): Option[String] = Some(permission.name)

  }

}
