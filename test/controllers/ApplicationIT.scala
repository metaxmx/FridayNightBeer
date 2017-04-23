package controllers


import org.scalatestplus.play.PlaySpec
import play.api.test._
import play.api.test.Helpers._

/**
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
class ApplicationIT extends PlaySpec {

  "Application" should {

    "send 404 on a bad request" in {
      val app = FakeApplication()
      running(app) {
        val invalid = route(app, FakeRequest(GET, "/boum")).get
        status(invalid) must be (NOT_FOUND)
      }
    }

    "render the index page" in {
      val app = FakeApplication()
      running(app) {
        val home = route(app, FakeRequest(GET, "/")).get
        status(home) must be (OK)
        contentType(home) must be (Some("text/html"))
      }
    }

  }
}