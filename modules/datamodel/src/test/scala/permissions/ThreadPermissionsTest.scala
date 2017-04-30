package permissions

import org.scalatest.{MustMatchers, WordSpec}
import ThreadPermissions._

/**
  * Test Thread Permissions
  */
class ThreadPermissionsTest extends WordSpec with MustMatchers {

  "The thread permissions" should {

    "have to correct type" in {
      name mustBe "ThreadPermission"
    }

    "contain the complete list" in {
      values.toSet mustBe Set(Access, Reply, EditPost, DeletePost, Attachment)
    }

    "contain the complete map" in {
      valuesByName mustBe Map(
        "Access" -> Access,
        "Reply" -> Reply,
        "EditPost" -> EditPost,
        "DeletePost" -> DeletePost,
        "Attachment" -> Attachment
      )
    }

    "return the correct value by apply" in {
      ThreadPermission("Access") mustBe Access
      ThreadPermission("Reply") mustBe Reply
      ThreadPermission("EditPost") mustBe EditPost
      ThreadPermission("DeletePost") mustBe DeletePost
      ThreadPermission("Attachment") mustBe Attachment
    }

    "return the correct value by unapply" in {
      ThreadPermission.unapply(Access) mustBe Some("Access")
      ThreadPermission.unapply(Reply) mustBe Some("Reply")
      ThreadPermission.unapply(EditPost) mustBe Some("EditPost")
      ThreadPermission.unapply(DeletePost) mustBe Some("DeletePost")
      ThreadPermission.unapply(Attachment) mustBe Some("Attachment")

      val matched = EditPost match {
        case ThreadPermission(e) => e
      }
      matched mustBe "EditPost"
    }

  }

}
