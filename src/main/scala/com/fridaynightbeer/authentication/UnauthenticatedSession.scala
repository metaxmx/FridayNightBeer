package com.fridaynightbeer.authentication

import com.fridaynightbeer.entity.UserEntity

case class UnauthenticatedSession(sessionId: String,
                                  globalPermissions: Set[String]) extends Session {

  override def authenticated: Boolean = false

  override def userOpt: Option[UserEntity] = None

}
