package services

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import model.HttpCall
import play.api.libs.json.JsString
import play.api.libs.ws.WSClient

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by jimbo on 09/02/17.
  */
@Singleton
class ActionCaller @Inject()(wsClient: WSClient,
                             system: ActorSystem) {

  implicit val implicitSystem = system

  def call(httpCalls: Seq[HttpCall]): Future[Boolean] = {
    val futures = httpCalls.map {
      case delayedHttpCall@HttpCall(_, _, _, Some(delay)) =>
        akka.pattern.after(delay.asDuration, system.scheduler)(httpFutureCall(delayedHttpCall))
      case httpCall: HttpCall => httpFutureCall(httpCall)
    }
    Future.sequence(futures).map(_ => true)
  }

  private def httpFutureCall(httpCall: HttpCall) = {

    val url = wsClient.url(httpCall.path)
    println("Calling: " + httpCall.path)
    httpCall.method.toUpperCase match {
      case "GET" =>  url.get()
      case "POST" => url.post(JsString(""))
    }
  }
}
