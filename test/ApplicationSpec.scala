import org.scalamock.scalatest.MockFactory
import org.scalatestplus.play._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.AnyContentAsEmpty
import play.api.test._
import play.api.test.Helpers._
import services.LircParser
import play.api.inject.bind
import play.api.libs.json.Json

class ApplicationSpec extends PlaySpec with MockFactory {

  val lircParser = mock[LircParser]
  val application = new GuiceApplicationBuilder()
    .disable[Module]
    .configure("authToken" -> "1234abcd")
//    .bindings(new ComponentModule)
    .bindings(bind[LircParser].toInstance(lircParser))
    .build

  "Routes" should {

    "send 404 on a bad request" in  {
      route(application, FakeRequest(GET, "/boum")).map(status(_)) mustBe Some(NOT_FOUND)
    }

  }

  "HomeController" should {

    "render the index page" in {
      val home = route(application, FakeRequest(GET, "/")).get

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include ("Your new application is ready.")
    }

    "not list IR devices if not authenticated" in {
      val fakeRequest = FakeRequest[AnyContentAsEmpty.type](method = GET, headers = FakeHeaders(), body = AnyContentAsEmpty, uri = "/ir")
      val response = route(application, fakeRequest).get

      status(response) mustBe FORBIDDEN
      contentAsString(response) must include ("Credentials must be supplied")
    }

    "list IR devices if authenticated" in {

      (lircParser.listDevices _).expects().returns(Seq("sony", "jvc"))

      val fakeRequest = FakeRequest[AnyContentAsEmpty.type](method = GET, headers = FakeHeaders(), body = AnyContentAsEmpty, uri = "/ir").withHeaders("authToken" -> "1234abcd")
      val irDevicesResponses = route(application, fakeRequest).get

      status(irDevicesResponses) mustBe OK
      contentAsJson(irDevicesResponses) must be (Json.toJson(Seq("sony", "jvc")))
    }
  }

}