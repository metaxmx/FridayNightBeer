package models

case class UserSession(_id: String,
                       user: Option[String]) extends BaseModel[UserSession] {

  def withUser(user_id: Option[String]) = copy(user = user)

  override def withId(_id: String) = copy(_id = _id)

}
