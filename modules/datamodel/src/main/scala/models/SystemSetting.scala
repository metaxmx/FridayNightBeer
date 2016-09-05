package models

case class SystemSetting(_id: String, value: String) extends BaseModel[SystemSetting] {

  override def withId(_id: String) = copy(_id = _id)

}
