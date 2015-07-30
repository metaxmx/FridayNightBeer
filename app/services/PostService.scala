package services

import javax.inject.{ Inject, Singleton }
import scala.concurrent.Future
import dao.PostDAO
import models.Post
import models.User
import exceptions.QueryException
import exceptions.ApiException.{ dbException, notFoundException }
import scala.concurrent.ExecutionContext.Implicits.global
import controllers.UserOptionRequest

@Singleton
class PostService @Inject() (postDAO: PostDAO) {

  def getPost(id: Int): Future[Option[Post]] = postDAO ?? id

  def getPostsByThread: Future[Map[Int, Seq[Post]]] = postDAO >> { _.groupBy { _.thread } }

  def insertPost(post: Post): Future[Post] = postDAO << post

  def getPostForApi(id: Int)(implicit userOpt: Option[User]): Future[Post] = getPost(id) map {
    case None       => notFoundException
    case Some(post) => post
  } recover {
    case e: QueryException => dbException
  }

  def getPostsByThreadForApi = getPostsByThread recover {
    case e: QueryException => dbException
  }

}