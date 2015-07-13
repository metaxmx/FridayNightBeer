package services

import javax.inject.{ Inject, Singleton }
import scala.concurrent.Future
import dao.PostDAO
import models.Post
import exceptions.QueryException
import exceptions.ApiException.dbException
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class PostService @Inject() (postDAO: PostDAO) {

  def getPost(id: Int): Future[Option[Post]] = postDAO ?? id

  def getPostsByThread: Future[Map[Int, Seq[Post]]] = postDAO >> { _.groupBy { _.thread } }

  def insertPost(post: Post): Future[Post] = postDAO << post
  
  def getPostsByThreadForApi = getPostsByThread recover {
    case e: QueryException => dbException
  }

}