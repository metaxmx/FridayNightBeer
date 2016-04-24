package controllers

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

/**
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
class ApplicationIT extends Specification {

  "Application" should {

    "send 404 on a bad request" in {
      val app = FakeApplication()
      running(app) {
        route(app, FakeRequest(GET, "/boum")) must beNone
      }
    }

    "render the index page" in {
      val app = FakeApplication()
      running(app) {
        val home = route(app, FakeRequest(GET, "/")).get
        status(home) must equalTo(OK)
        contentType(home) must beSome.which(_ == "text/html")
      }
    }

  }
}