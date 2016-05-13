package rest.api_1_0.viewmodels

import org.joda.time.DateTime

/**
  * Created by Christian Simon on 14.05.2016.
  */
object ThreadsViewModels {

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


}
