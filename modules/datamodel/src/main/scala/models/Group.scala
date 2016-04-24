package models

case class Group(_id: String,
                 name: String) extends BaseModel[Group] {

  override def withId(_id: String) = copy(_id = _id)

}