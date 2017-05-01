package models

case class User(_id: String,
                username: String,
                password: String,
                displayName: String,
                email: String,
                fullName: Option[String],
                avatar: Option[String],
                groups: Option[Seq[String]]) extends BaseModel[User] {

  def withId(_id: String): User = copy(_id = _id)

}
