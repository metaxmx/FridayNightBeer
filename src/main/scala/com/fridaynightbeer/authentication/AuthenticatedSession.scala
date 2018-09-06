package com.fridaynightbeer.authentication

import com.fridaynightbeer.entity.UserEntity

case class AuthenticatedSession(sessionId: String,
                                user: UserEntity,
                                globalPermissions: Set[String]) extends Session {

  override def authenticated = true

  override def userOpt: Option[UserEntity] = Some(user)

}
