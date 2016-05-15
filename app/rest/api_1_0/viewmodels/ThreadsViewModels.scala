package rest.api_1_0.viewmodels

import org.joda.time.DateTime

/**
  * Created by Christian Simon on 14.05.2016.
  */
object ThreadsViewModels {

  /*
   * Show Thread
   */

  case class ShowThreadPostUpload(filename: String,
                                  size: Long,
                                  hits: Int) extends ViewModel

  case class ShowThreadPost(id: String,
                            date: DateTime,
                            user: String,
                            userName: String,
                            userFullname: Option[String],
                            userAvatar: Boolean,
                            content: String,
                            uploads: Option[Seq[ShowThreadPostUpload]]) extends ViewModel

  case class ShowThreadResult(success: Boolean,
                              id: String,
                              title: String,
                              forum: String,
                              forumTitle: String,
                              posts: Seq[ShowThreadPost]) extends ViewModel

  /*
   * Create Thread
   */

  case class CreateThreadRequest(title: String,
                                 firstPostContent: String,
                                 sticky: Boolean,
                                 close: Boolean) extends ViewModel


  case class CreateThreadResult(success: Boolean,
                                id: String) extends ViewModel

  /*
   * Create Post
   */

  case class CreatePostRequest(content: String,
                               makeSticky: Boolean,
                               close: Boolean) extends ViewModel


  case class CreatePostResult(success: Boolean) extends ViewModel

}
