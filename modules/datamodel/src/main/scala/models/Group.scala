package models

case class Group(_id: String,
                 name: String,
                 description: String) extends BaseModel[Group] {

  override def withId(_id: String): Group = copy(_id = _id)

}