package rest.api_1_0.viewmodels

import models.{Forum, ForumCategory, Thread, User}
import org.joda.time.DateTime
import permissions.{Authorization, ForumPermissions}
import permissions.ForumPermissions.{Access => FAccess}
import permissions.ThreadPermissions.{Access => TAccess}

/**
  * Created by Christian Simon on 11.05.2016.
  */
object ForumsViewModels {

  case class ForumInfoLastPost(id: String,
                               title: String,
                               user: String,
                               userName: String,
                               date: DateTime) extends ViewModel

  case class ForumInfoForum(id: String,
                            name: String,
                            description: Option[String],
                            numThreads: Int,
                            numPosts: Int,
                            lastPost: Option[ForumInfoLastPost]) extends ViewModel

  case class ForumInfoCategory(id: String,
                               name: String,
                               forums: Seq[ForumInfoForum]) extends ViewModel

  case class ForumInfoResult(success: Boolean,
                             categories: Seq[ForumInfoCategory]) extends ViewModel


  def createForumInfo(categories: Seq[ForumCategory], forums: Seq[Forum],
                      threads: Seq[Thread], users: Map[String, User])(implicit authorization: Authorization): ForumInfoResult = {
    ForumInfoResult(success = true, categories.sortBy(_.position) map {
      cat =>
        val catForums = forums.filter(_.category == cat._id).filter(forum => authorization.checkForumPermission(cat, forum, FAccess)).sortBy(_.position)
        ForumInfoCategory(cat._id, cat.name, catForums map {
          forum =>
            val forumThreads = threads.filter(_.forum == forum._id).filter(thread => authorization.checkThreadPermission(cat, forum, thread, TAccess))
            val lastPostThread = forumThreads.sortBy { _.lastPost.date }.reverse.headOption
            ForumInfoForum(forum._id, forum.name, forum.description, forumThreads.length, forumThreads.map(_.posts).sum,
              for {
                thread <- lastPostThread
                user <- users.get(thread.lastPost.user)
              } yield ForumInfoLastPost(thread._id, thread.title, thread.lastPost.user, user.displayName, thread.lastPost.date)
              )
        })
    } filter {
      _.forums.nonEmpty
    })
  }


}
