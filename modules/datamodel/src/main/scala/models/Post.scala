package models

import org.joda.time.DateTime

case class PostEdit(user: String,
                    date: DateTime,
                    reason: Option[String],
                    ip: String)

case class Post(_id: String,
                thread: String,
                text: String,
                userCreated: String,
                dateCreated: DateTime,
                edits: Option[Seq[PostEdit]],
                uploads: Seq[PostUpload]) extends BaseModel[Post] {

  override def withId(_id: String) = copy(_id = _id)

}
