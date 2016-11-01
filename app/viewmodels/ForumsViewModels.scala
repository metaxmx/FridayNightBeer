package viewmodels

import org.joda.time.DateTime

/**
  * Created by Christian Simon on 11.05.2016.
  */
object ForumsViewModels {

  /*
   * Forum overview
   */

  case class ForumOverviewLastPost(id: String,
                                   title: String,
                                   user: String,
                                   userName: String,
                                   date: DateTime) extends ViewModel

  case class ForumOverviewForum(id: String,
                                url: String,
                                name: String,
                                description: Option[String],
                                numThreads: Int,
                                numPosts: Int,
                                lastPost: Option[ForumOverviewLastPost]) extends ViewModel

  case class ForumOverviewCategory(id: String,
                                   name: String,
                                   forums: Seq[ForumOverviewForum]) extends ViewModel

  case class ForumOverviewResult(success: Boolean,
                                 categories: Seq[ForumOverviewCategory]) extends ViewModel


  /*
   * Show Forum
   */

  case class ShowForumPost(user: String,
                           userName: String,
                           date: DateTime) extends ViewModel

  case class ShowForumThread(id: String,
                             url: String,
                             title: String,
                             posts: Int,
                             sticky: Boolean,
                             firstPost: ShowForumPost,
                             latestPost: ShowForumPost) extends ViewModel

  case class ShowForumResult(success: Boolean,
                             id: String,
                             title: String,
                             threads: Seq[ShowForumThread],
                             permissions: Seq[String]) extends ViewModel

  /*
   * Show Forum Head
   */

  case class ShowForumHeadResult(success: Boolean,
                                 id: String,
                                 url: String,
                                 title: String,
                                 permissions: Seq[String]) extends ViewModel

}
