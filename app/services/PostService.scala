package services

import javax.inject.{Inject, Singleton}

import models.Post
import storage.PostDAO
import util.FutureOption

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class PostService @Inject()(postDAO: PostDAO) {

  def getPost(id: String): FutureOption[Post] = postDAO ?? id

  def getPostsByThread: Future[Map[String, Seq[Post]]] = postDAO >> (_.groupBy(_.thread))

  def getPostsForThread(threadId: String): Future[Seq[Post]] = getPostsByThread map (_.getOrElse(threadId, Seq.empty))

  def insertPost(post: Post): Future[Post] = postDAO << post

}