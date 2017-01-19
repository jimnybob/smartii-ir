package auth

import org.apache.commons.net.util.SubnetUtils
import play.api.libs.json.Json
import play.api.mvc.Results.Forbidden
import play.api.mvc._
import play.api.{Configuration, Environment, Mode}

import scala.concurrent.Future
import scala.util.Try

class AuthenticatedRequest[A](request: Request[A]) extends WrappedRequest[A](request)

object AuthenticatedAction {


  def apply(conf: Configuration, env: Environment) = {

    new ActionBuilder[Request] {

      override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = authenticate(request, block)

      /**
        * Authenticate the given block.
        */
      def authenticate[A](request: Request[A], block: (AuthenticatedRequest[A]) => Future[Result]) = {

        def isDevelopment = (env.mode == Mode.Dev)

        /**
          * Not exactly a bomb-proof implementation but it'll do. The 'domain' field won't exist for unit testing
          * @return
          */
        def isSameNetwork = Try { new SubnetUtils(s"${request.domain}/24").getInfo.isInRange(request.remoteAddress) }.getOrElse(false)

        def isGoodAuthToken = request.headers.get("authToken") == conf.getString("authToken")

        isDevelopment ||  isGoodAuthToken || isSameNetwork match {
          case true => block(new AuthenticatedRequest(request))
          case false => Future.successful(Forbidden(Json.toJson("Credentials must be supplied")))
        }
      }

    }

  }
}