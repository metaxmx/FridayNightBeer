package models

case class UserSession(_id: String,
                       user: Option[String]) extends BaseModel[UserSession] {

  def withUser(user: Option[String]): UserSession = copy(user = user)

  override def withId(_id: String): UserSession = copy(_id = _id)

}
