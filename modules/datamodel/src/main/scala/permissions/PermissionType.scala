package permissions

import scala.reflect.{ClassTag, classTag}

abstract class PermissionType[T <: PermissionEnum : ClassTag] {

  val name = classTag[T].runtimeClass.getSimpleName

  def values: Seq[T]

  lazy val valuesByName = values.map { value => value.name -> value }.toMap

  def apply(name: String): T = valuesByName(name)

  def unapply(permission: T): Option[String] = Some(permission.name)

}
