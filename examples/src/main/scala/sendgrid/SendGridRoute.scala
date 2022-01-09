package sendgrid

import akka.NotificationManager.Result
import akka.RootManager.{Notify, PropertyContext}
import akka.ServerRoute
import akka.actor.ActorRef
import akka.http.scaladsl.model.{HttpHeader, StatusCodes}
import akka.http.scaladsl.server.Directives.{as, complete, concat, entity, headerValue, onSuccess, pathPrefix, post}
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import sendgrid.SendGridEmailSender.SendGridEmailRequest
import spray.json.DefaultJsonProtocol.{StringJsonFormat, jsonFormat6}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import java.time.Duration

object SendGridRoute extends ServerRoute {

  def extractApiKey: HttpHeader => Option[String] = {
    case HttpHeader("sendgrid_api_key", value: String) => Some(value)
    case _ => None
  }

  override def buildRoute: ActorRef => Route = rootActor => {
    pathPrefix("notify") {
      pathPrefix("email") {
        concat(
          pathPrefix("sendgrid") {
            post {
              headerValue(extractApiKey) { api_key =>
                implicit val listOfString: RootJsonFormat[List[String]] = DefaultJsonProtocol.listFormat[String]
                implicit val sendGridEmailRequestM: RootJsonFormat[SendGridEmailRequest] = jsonFormat6(SendGridEmailRequest)
                implicit val timeout: Timeout = Timeout.create(Duration.ofSeconds(15))
                entity(as[SendGridEmailRequest]) {
                  case sendgridGridRequest: SendGridEmailRequest =>
                    onSuccess(rootActor ? Notify(PropertyContext.fromMap(Map("SENDGRID_API_KEY" -> api_key)), sendgridGridRequest)) {
                      case result: Result => complete(
                        StatusCodes.custom(
                          result.response.statusCode,
                          s"Request processed at [${result.response.timeStamp}] by [${result.replyFrom}]",
                          result.response.message
                        )
                      )
                      case _ => complete(StatusCodes.InternalServerError -> "Request could not be processed")
                    }
                  case _ => complete(StatusCodes.BadRequest -> "Incorrect arguments for send grid request")
                }
              }
            }
          }
        )
      }
    }
  }

}
