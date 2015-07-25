package models

object GlobalPermissions extends Enumeration {

  type GlobalPermission = Value

  val Forum = Value
  val Media = Value
  val Events = Value
  val Members = Value
  val Admin = Value

  def hasName(name: String) = values.find(_.toString == name).isDefined

}

object ForumPermissions extends Enumeration {

  type ForumPermission = Value

  val Access = Value
  val NewTopic = Value
  val Reply = Value
  val Sticky = Value
  val Close = Value

  def hasName(name: String) = values.find(_.toString == name).isDefined

}
