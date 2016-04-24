package services

import javax.inject.{Inject, Singleton}

import exceptions.ApiExceptions.{dbException, notFoundException}
import exceptions.QueryException
import models.{Post, User}
import storage.PostDAO

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class PostService @Inject()(postDAO: PostDAO) {

  def getPost(id: String): Future[Option[Post]] = postDAO ?? id

  def getPostsByThread: Future[Map[String, Seq[Post]]] = postDAO >> (_.groupBy(_.thread))

  def insertPost(post: Post): Future[Post] = postDAO << post

  def getPostForApi(id: String)(implicit userOpt: Option[User]): Future[Post] = getPost(id) map {
    case None => notFoundException
    case Some(post) => post
  } recover {
    case e: QueryException => dbException(e)
  }

  def getPostsByThreadForApi = getPostsByThread recover {
    case e: QueryException => dbException(e)
  }

}