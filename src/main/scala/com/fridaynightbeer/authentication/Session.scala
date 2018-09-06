package com.fridaynightbeer.authentication

import java.util.UUID

import com.fridaynightbeer.entity.UserEntity

/**
  * User session abstraction.
  */
trait Session {

  /** The ID of the session */
  def sessionId: String

  /** If the session authenticated, e.g. assigned to a user? */
  def authenticated: Boolean

  def userOpt: Option[UserEntity]

  /** Global permissions of the principle */
  def globalPermissions: Set[String]

}

object Session {

  /**
    * Create new, random session id.
    *
    * @return new session id
    */
  def createSessionId(): String = UUID.randomUUID().toString

}