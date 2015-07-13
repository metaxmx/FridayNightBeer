package services

import javax.inject.{ Inject, Singleton }

import scala.concurrent.Future

import dao.PostDAO
import models.Post

@Singleton
class PostService @Inject() (postDAO: PostDAO) {

  def getPost(id: Int): Future[Option[Post]] = postDAO ?? id

  def getPostsByThread: Future[Map[Int, Seq[Post]]] = postDAO >> { _.groupBy { _.thread } }

  def insertPost(post: Post): Future[Post] = postDAO << post

}