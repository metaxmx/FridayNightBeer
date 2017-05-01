package models

import authentication.PermissionAuthorization
import org.joda.time.DateTime
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}
import permissions.{ForumPermissions, GlobalPermissions, ThreadPermissions}

/**
  * Test for thread model
  */
class ThreadTest extends WordSpec with MustMatchers with MockitoSugar {

  "A thread model" when {
    "having no thread permissions" should {
      "have an empty thread permission map" in new TestThreadFixture {
        val threadPermissions: Option[Map[String, AccessRule]] = None
        val thread = testThread(threadPermissions)
        thread.threadPermissionMap mustBe empty
      }
    }
    "having empty thread permissions" should {
      "have an empty thread permission map" in new TestThreadFixture {
        val threadPermissions: Option[Map[String, AccessRule]] = Some(Map.empty)
        val thread = testThread(threadPermissions)
        thread.threadPermissionMap mustBe empty
      }
    }
    "having some thread permissions" should {
      "reflect in thread permission map" in new TestThreadFixture {
        val threadPermissions: Option[Map[String, AccessRule]] = Some(Map(ThreadPermissions.Access.name -> AccessRule.empty))
        val thread = testThread(threadPermissions)
        thread.threadPermissionMap mustBe Map(ThreadPermissions.Access.name -> AccessRule.empty)
      }
    }
    "calling withId" should {
      "return an identical copy, except for the id" in new TestThreadFixture {
        val newId = "newId"
        val thread = testThread
        val changedThread = thread.withId(newId)
        changedThread._id mustBe newId
        changedThread mustBe thread.copy(_id = newId)

      }
    }
    "calling withLastPost" should {
      "return an identical copy, except for the lastPost" in new TestThreadFixture {
        val changedDate = DateTime.parse("2010-06-30T01:20+02:00")
        val lastPost = ThreadPostData("anotherUser", changedDate)
        val thread = testThread
        val changedThread = thread.withLastPost(lastPost)
        changedThread.lastPost mustBe lastPost

      }
    }
    "calling checkAccess" when {
      "forum access denied and thread access denied" should {
        "deny access" in new ThreadAuthFixture {
          denyForumAccess()
          denyThreadAccess()

          thread.checkAccess(category, forum)(auth) mustBe false
          thread.checkAccess(auth, category, forum) mustBe false

          verifyForumAccessNotCalled()
          verifyThreadAccessCalled()
        }
      }
      "forum access denied and thread access allowed" should {
        "deny access" in new ThreadAuthFixture {
          denyForumAccess()
          allowThreadAccess()

          thread.checkAccess(category, forum)(auth) mustBe false
          thread.checkAccess(auth, category, forum) mustBe false

          verifyForumAccessNegativeCalled()
          verifyThreadAccessCalled()
        }
      }
      "forum access allowed and thread access denied" should {
        "deny access" in new ThreadAuthFixture {
          allowForumAccess()
          denyThreadAccess()

          thread.checkAccess(category, forum)(auth) mustBe false
          thread.checkAccess(auth, category, forum) mustBe false

          verifyForumAccessNotCalled()
          verifyThreadAccessCalled()
        }
      }
      "forum access allowed and thread access allowed" should {
        "allow access" in new ThreadAuthFixture {
          allowForumAccess()
          allowThreadAccess()

          thread.checkAccess(category, forum)(auth) mustBe true
          thread.checkAccess(auth, category, forum) mustBe true

          verifyForumAccessCalled()
          verifyThreadAccessCalled()
        }
      }
    }
  }

  "Thread models" should {
    "be sorted by sticky, lastPost, id" in {
      val dateNew = DateTime.now()
      val dateMiddle = dateNew.minusWeeks(1)
      val dateOld = dateMiddle.minusYears(2)

      def testThread(id: String, date: DateTime, sticky: Boolean): Thread = {
        val lastPostData = ThreadPostData("testUser", date)
        val firstPostData = ThreadPostData("anotherUser", DateTime.now().minusYears(10))
        Thread(id, "Test Thread", None, "testforum", firstPostData, lastPostData, 33, sticky, closed = false, None)
      }

      val thread1 = testThread("x", dateMiddle, sticky = true)
      val thread2 = testThread("y", dateOld, sticky = true)
      val thread3 = testThread("v", dateNew, sticky = false)
      val thread4 = testThread("s", dateMiddle, sticky = false)
      val thread5 = testThread("t", dateMiddle, sticky = false)
      val thread6 = testThread("m", dateOld, sticky = false)

      val seq = Seq(thread4, thread3, thread6, thread1, thread2, thread5)
      val sorted = seq.sorted
      sorted mustBe Seq(thread1, thread2, thread3, thread4, thread5, thread6)
    }
  }

  trait TestThreadFixture {

    def testPostData: ThreadPostData = ThreadPostData("testUser", DateTime.now())

    def testThread: Thread = testThread(None)

    def testThread(threadPermissions: Option[Map[String, AccessRule]]): Thread = {
      Thread("test", "Test", None, "testForum", testPostData, testPostData, 23, sticky = false, closed = false, threadPermissions)
    }

    def testCategory: ForumCategory = ForumCategory("testcat", "Test Category", 0, None, None)

    def testForum: Forum = Forum("test", "Test Forum", None, None, "testcat", 0, readonly = false, None, None)

  }

  trait ThreadAuthFixture extends TestThreadFixture {

    val auth = mock[PermissionAuthorization]
    val category = testCategory
    val forum = testForum
    val thread = testThread

    def allowForumAccess(): Unit = {
      when(auth.checkGlobalPermissions(GlobalPermissions.Forums)).thenReturn(true)
      when(auth.checkForumPermissions(category, forum, ForumPermissions.Access)).thenReturn(true)
    }

    def denyForumAccess(): Unit = {
      when(auth.checkGlobalPermissions(GlobalPermissions.Forums)).thenReturn(false)
      when(auth.checkForumPermissions(category, forum, ForumPermissions.Access)).thenReturn(false)
    }

    def allowThreadAccess(): Unit = {
      when(auth.checkThreadPermissions(category, forum, thread, ThreadPermissions.Access)).thenReturn(true)
    }

    def denyThreadAccess(): Unit = {
      when(auth.checkThreadPermissions(category, forum, thread, ThreadPermissions.Access)).thenReturn(false)
    }

    def verifyForumAccessCalled(): Unit = {
      verify(auth, times(2)).checkGlobalPermissions(GlobalPermissions.Forums)
      verify(auth, times(2)).checkForumPermissions(category, forum, ForumPermissions.Access)
    }

    def verifyForumAccessNegativeCalled(): Unit = {
      verify(auth, never()).checkGlobalPermissions(GlobalPermissions.Forums)
      verify(auth, times(2)).checkForumPermissions(category, forum, ForumPermissions.Access)
    }

    def verifyForumAccessNotCalled(): Unit = {
      verify(auth, never()).checkGlobalPermissions(GlobalPermissions.Forums)
      verify(auth, never()).checkForumPermissions(category, forum, ForumPermissions.Access)
    }

    def verifyThreadAccessCalled(): Unit = {
      verify(auth, times(2)).checkThreadPermissions(category, forum, thread, ThreadPermissions.Access)
    }

  }

}
