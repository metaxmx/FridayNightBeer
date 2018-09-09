package com.fridaynightbeer.controller

import com.fridaynightbeer.authentication.AuthenticationStorage

class SessionController(authStorage: AuthenticationStorage) {



}

object SessionController {

  def apply(authStorage: AuthenticationStorage = AuthenticationStorage.globalAuthStorage): SessionController = {
    new SessionController(authStorage)
  }

}