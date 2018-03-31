package models

import org.joda.time.DateTime
import util.JodaOrdering.dateTimeOrdering

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

  override def withId(_id: String): Post = copy(_id = _id)

}

object Post {

  private[this] val explicitTupleOrdering: Ordering[(DateTime, String)] = Ordering.Tuple2(
    implicitly[Ordering[DateTime]].reverse, // First order by post time (descending)
    implicitly[Ordering[String]]) // Fallback on id, if all else is equal

  implicit val postOrdering: Ordering[Post] = Ordering.by {
    post: Post =>
      (post.dateCreated, post._id)
  }(explicitTupleOrdering)


}