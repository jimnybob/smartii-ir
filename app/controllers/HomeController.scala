package controllers

import javax.inject._

import play.api._
import play.api.libs.json.Json
import play.api.mvc._
import services.LircParser

import scala.util.{Failure, Success, Try}

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(lircParser: LircParser) extends Controller {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def listDevices: Action[AnyContent] = Action {

    Try { lircParser.listDevices } match {
      case Success(devices) => Ok(Json.toJson(devices))
      case Failure(error) => InternalServerError(Json.toJson(s"Error listing devices: $error"))
    }
  }

  def listButtons(device: String): Action[AnyContent] = Action {

    Try { lircParser.listButtons(device) } match {
      case Success(buttons) => Ok(Json.toJson(buttons))
      case Failure(error) => InternalServerError(Json.toJson(s"Error listing buttons for '$device': $error"))
    }
  }

  def pressButton(device: String, button: String): Action[AnyContent] = Action {

    Try { lircParser.pressButton(device, button) } match {
      case Success(true) =>  Ok(Json.toJson("Success"))
      case Success(false) => InternalServerError(Json.toJson(s"Error pressing button '$button' for '$device'"))
      case Failure(error) => InternalServerError(Json.toJson(s"Error pressing button '$button' for '$device': $error"))
    }
  }

}
