package models

import org.joda.time.DateTime
import org.scalatest.{MustMatchers, WordSpec}

/**
  * Test for Post Model
  */
class PostTest extends WordSpec with MustMatchers {

  "A post model" when {
    "calling withId" should {
      "return an identical copy, except for the id" in {
        val newId = "newId"
        val post = Post("testId", "threadId", "My text", "testuser", DateTime.now(), None, Seq.empty)
        val changedPost = post.withId(newId)
        changedPost._id mustBe newId
        changedPost mustBe post.copy(_id = newId)
      }
    }
  }

  "Post models" should {
    "be sorted by date" in {
      val dateNew = DateTime.now()
      val dateMiddle = dateNew.minusWeeks(1)
      val dateOld = dateMiddle.minusYears(2)
      val post1 = Post("a", "threadId", "My text", "testuser", dateOld, None, Seq.empty)
      val post2 = Post("b", "threadId", "My text", "testuser", dateMiddle, None, Seq.empty)
      val post3 = Post("c", "threadId", "My text", "testuser", dateNew, None, Seq.empty)
      val seq = Seq(post3, post1, post2)
      val sorted = seq.sorted
      sorted mustBe Seq(post3, post2, post1)
    }
    "be sorted by id if date is equal" in {
      val date = DateTime.now()
      val post1 = Post("a", "threadId", "My text", "testuser", date, None, Seq.empty)
      val post2 = Post("b", "threadId", "My text", "testuser", date, None, Seq.empty)
      val post3 = Post("c", "threadId", "My text", "testuser", date, None, Seq.empty)
      val seq = Seq(post2, post3, post1)
      val sorted = seq.sorted
      sorted mustBe Seq(post1, post2, post3)
    }
  }

}
