package model

import java.util.concurrent.TimeUnit

import play.api.libs.json._

import scala.concurrent.duration.FiniteDuration
import scala.util.Try

/**
  *  Duplicated from smartii-home-alexa project uk.co.smartii.alexa.model.HttpCall
  */
case class HttpCall(method: String, path: String, order: Int, delay: Option[Delay]= None)

object HttpCall {
  implicit val formatTimeUnit = Delay.timeUnitFormat
  implicit val formatDelayJson = Json.format[Delay]
  implicit val formatJson = Json.format[HttpCall]
}

case class Delay(time: Long, units: TimeUnit) {
  def asDuration = FiniteDuration(time, units)
}

object Delay {
  implicit val timeUnitReads: Reads[TimeUnit] = new Reads[TimeUnit] {
    def reads(json: JsValue): JsResult[TimeUnit] = json match {
      case JsString(s) => Try(JsSuccess(TimeUnit.valueOf(s)))
        .getOrElse(JsError(s"Enumeration expected of type: 'TimeUnit', but it does not appear to contain the value: '$s'"))
      case _ => JsError("String value expected")
    }
  }

  implicit val timeUnitWrites: Writes[TimeUnit] = Writes(v => JsString(v.toString))

  implicit val timeUnitFormat: Format[TimeUnit] = Format(timeUnitReads, timeUnitWrites)

  implicit val formatJson = Json.format[Delay]
}