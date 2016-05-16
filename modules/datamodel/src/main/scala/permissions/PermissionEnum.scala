package permissions

/**
  * Created by Christian Simon on 04.05.2016.
  */
trait PermissionEnum {

  val name = toString

}

abstract class PermissionType {

  def permissionType: Class[_]

  val name = permissionType.getSimpleName

}