package services

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.testkit.TestKit
import com.typesafe.config.ConfigFactory
import model.{Delay, HttpCall}
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpecLike, Matchers}
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}

import scala.concurrent.duration._
import org.scalatest.concurrent.{ScalaFutures}
import play.api.libs.json.Json
import play.mvc.Http.Status

import scala.concurrent.Future

/**
  * Created by jimbo on 09/02/17.
  */
object ActionCallerSpec {
  // Define your test specific configuration here
  val config =
    """
    akka {
      loglevel = "WARNING"
    }
    """
}

class ActionCallerSpec extends TestKit(ActorSystem("InfraRedServiceSpec", ConfigFactory.parseString(ActionCallerSpec.config))) with FlatSpecLike with Matchers with MockFactory with ScalaFutures {

  "The ActionCaller service" should "handle multiple http calls" in {

    val ws = mock[WSClient]

    val actionCaller = new DefaultActionCaller(ws, system)

    val actions = Seq(HttpCall(method = "GET", path = "/jvc/AUX", order = 0), HttpCall(method = "GET", path = "/jvc/OFF", order = 1, delay = Some(Delay(3, TimeUnit.SECONDS))))

    val mockRequest = mock[WSRequest]
    val mockResponse = mock[WSResponse]
//    (mockResponse.status _).expects().returns(Status.OK).anyNumberOfTimes()
    (mockRequest.get: () => Future[WSResponse]).expects().returns(Future.successful(mockResponse)).anyNumberOfTimes()

    (ws.url _).expects("/jvc/AUX").returns(mockRequest)
    (ws.url _).expects("/jvc/OFF").returns(mockRequest)

    actionCaller.call(actions).futureValue(timeout(4 seconds))
  }
}
