package rest.api_1_0.controllers

import javax.inject.{Inject, Singleton}

import models._
import org.joda.time.DateTime.now
import permissions.{ForumPermissions, GlobalPermissions, ThreadPermissions}
import play.api.Logger
import rest.Exceptions.NotFoundException
import rest.Implicits._
import rest.api_1_0.viewmodels.ThreadsViewModels._
import services._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by Christian Simon on 14.05.2016.
  */
@Singleton
class ThreadController @Inject()(val userService: UserService,
                                 val sessionService: SessionService,
                                 val permissionService: PermissionService,
                                 forumService: ForumService,
                                 forumCategoryService: ForumCategoryService,
                                 threadService: ThreadService,
                                 postService: PostService) extends RestController {

  override def requiredGlobalPermission = Some(GlobalPermissions.Forums)

  def showThread(id: String) = OptionalSessionRestAction.async {
    implicit request =>
      mapOk(getShowThread(id))
  }

  def createThread(forumId: String) = UserRestAction.async(jsonREST[CreateThreadRequest]) {
    implicit request =>
      mapOk(processCreateThread(forumId))
  }

  def createPost(threadId: String) = UserRestAction.async(jsonREST[CreatePostRequest]) {
    implicit request =>
      mapOk(processCreatePost(threadId))
  }

  private[this] def getShowThread(threadId: String)(implicit request: OptionalSessionRequest[_]): Future[ShowThreadResult] =
    for {
      thread <- threadService.getThreadOrElse(threadId, throw NotFoundException(s"Thread not found: $threadId"))
      forum <- forumService.getForumOrElse(thread.forum, throw NotFoundException(s"Forum not found: ${thread.forum}"))
      category <- forumCategoryService.getCategoryOrElse(forum.category, throw NotFoundException(s"Category not found: ${forum.category}"))
      _ <- requirePermissionCheck(thread.checkAccess(category, forum))
      posts <- postService.getPostsForThread(threadId)
      userIndex <- userService.getUserIndex
    } yield {
      val postViewModels = posts.filter(userIndex contains _.userCreated).sorted.map {
        post =>
          val user = userIndex(post.userCreated)
          val uploads = post.uploads.map {
            upload =>
              ShowThreadPostUpload(upload.filename, upload.size, upload.hits)
          }
          ShowThreadPost(post._id, post.dateCreated, user._id, user.displayName, user.fullName, user.avatar, post.text,
            Some(uploads).filter(_.nonEmpty))
      }
      ShowThreadResult(success = true, thread._id, thread.title, forum._id, forum.name, postViewModels)
    }

  private[this] def processCreateThread(forumId: String)(implicit request: UserRequest[CreateThreadRequest]): Future[CreateThreadResult] =
    for {
      forum <- forumService.getForumOrElse(forumId, throw NotFoundException(s"Forum not found: $forumId"))
      category <- forumCategoryService.getCategoryOrElse(forum.category, throw NotFoundException(s"Category not found: ${forum.category}"))
      _ <- requireForumPermissions(category, forum, ForumPermissions.CreateThread)
      thread <- insertThread(category, forum)
    } yield {
      CreateThreadResult(success = true, thread._id)
    }

  private[this] def processCreatePost(threadId: String)(implicit request: UserRequest[CreatePostRequest]): Future[CreatePostResult] =
    for {
      thread <- threadService.getThreadOrElse(threadId, throw NotFoundException(s"Thread not found: $threadId"))
      forum <- forumService.getForumOrElse(thread.forum, throw NotFoundException(s"Forum not found: ${thread.forum}"))
      category <- forumCategoryService.getCategoryOrElse(forum.category, throw NotFoundException(s"Category not found: ${forum.category}"))
      _ <- requireThreadPermissions(category, forum, thread, ThreadPermissions.Access, ThreadPermissions.Reply)
      // TODO: Check if thread is not closed
      _ <- insertPost(category, forum, thread)
    } yield {
      CreatePostResult(success = true)
    }

  private[this] def insertThread(category: ForumCategory, forum: Forum)(implicit request: UserRequest[CreateThreadRequest]): Future[Thread] = {
    val createThreadRequest: CreateThreadRequest = request.body
    val sticky = createThreadRequest.sticky && request.authorization.checkForumPermissions(category, forum, ForumPermissions.Sticky)
    val closed = createThreadRequest.close && request.authorization.checkForumPermissions(category, forum, ForumPermissions.Close)
    val threadStart = new ThreadPostData(request.user._id, now)
    val lastPost = threadStart.copy()
    // TODO: Initial Thread permissions (e.g. only visible to group, for birthday threads)
    val thread = new Thread(_id = "", createThreadRequest.title, forum._id, threadStart, lastPost, posts = 1, sticky, closed, None)
    for {
      insertedThread <- threadService.insertThread(thread)
      insertedPost <- {
        // TODO: Uploads
        val post = new Post(_id = "", insertedThread._id, createThreadRequest.firstPostContent, threadStart.user, threadStart.date, None, Seq.empty)
        postService.insertPost(post)
      }
    } yield {
      Logger.info(s"Create Thread with title ${insertedThread.title} -> _id = ${insertedThread._id}")
      Logger.info(s"HTML is: ${insertedPost.text}")
      insertedThread
    }
  }

  private[this] def insertPost(category: ForumCategory, forum: Forum, thread: Thread)(implicit request: UserRequest[CreatePostRequest]): Future[Unit] = {
    val createPostRequest: CreatePostRequest = request.body
    val sticky = (createPostRequest.makeSticky && request.authorization.checkForumPermissions(category, forum, ForumPermissions.Sticky)) || thread.sticky
    val closed = (createPostRequest.close && request.authorization.checkForumPermissions(category, forum, ForumPermissions.Close)) || thread.closed
    // TODO: Uploads
    val post = new Post(_id = "", thread._id, createPostRequest.content, request.user._id, now, None, Seq.empty)
    for {
      insertedPost <- postService.insertPost(post)
      postsAfterInsert <- postService.getPostsForThread(thread._id)
      _ <- threadService.updateLastPost(thread._id, post.userCreated, post.dateCreated).toFuture
      _ <- updateSticky(thread, sticky)
      _ <- updateClosed(thread, closed)
    } yield Unit
  }

  private[this] def updateSticky(thread: Thread, sticky: Boolean): Future[Unit] = {
    if (thread.sticky == sticky)
      Future.successful(Unit)
    else
      threadService.updateSticky(thread._id, sticky).toFuture.map(_ => Unit)
  }

  private[this] def updateClosed(thread: Thread, closed: Boolean): Future[Unit] = {
    if (thread.closed == closed)
      Future.successful(Unit)
    else
      threadService.updateClosed(thread._id, closed).toFuture.map(_ => Unit)
  }

}
