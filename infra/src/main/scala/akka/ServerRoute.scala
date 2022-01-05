package akka

import akka.NotificationManager.Result
import akka.RootManager.{GetClusterMembers, Notify, PropertyContext}
import akka.actor.ActorRef
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{HttpHeader, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import sendgrid.SendGridEmailSender.SendGridEmailRequest
import spray.json.DefaultJsonProtocol.{StringJsonFormat, jsonFormat6}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import java.time.Duration
import java.util.concurrent.Executors
import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService}


trait JsonSupport extends SprayJsonSupport {
  implicit val listOfString: RootJsonFormat[List[String]] = DefaultJsonProtocol.listFormat[String]
  implicit val sendGridEmailRequestM: RootJsonFormat[SendGridEmailRequest] = jsonFormat6(SendGridEmailRequest)
}

class ServerRoute(rootManager: ActorRef) extends JsonSupport {

  implicit private val executionContext: ExecutionContextExecutorService = ExecutionContext.fromExecutorService(Executors.newCachedThreadPool())
  implicit private val timeout: Timeout = Timeout.create(Duration.ofSeconds(15))

  def extractApiKey: HttpHeader => Option[String] = {
    case HttpHeader("sendgrid_api_key", value: String) => Some(value)
    case _ => None
  }

  def getRoute: Route = concat(
    pathPrefix("health")(healthRoute),
    pathPrefix("notify")(notificationRoute)
  )

  private val healthRoute: Route = concat(
    pathEnd {
      concat(
        get {
          complete(StatusCodes.OK)
        }
      )
    },
    path("nodes") {
      concat(
        get {
          val members = (rootManager ? GetClusterMembers).mapTo[List[String]]
          onSuccess(members) {
            case members: List[String] => complete(members)
            case _ => complete(StatusCodes.InternalServerError -> "No nodes could be found")
          }
        }
      )
    }
  )

  private val notificationRoute: Route = pathPrefix("email") {
    concat(
      pathPrefix("sendgrid") {
        post {
          headerValue(extractApiKey) { api_key =>
            entity(as[SendGridEmailRequest]) {
              case sendgridGridRequest: SendGridEmailRequest =>
                onSuccess(rootManager ? Notify(PropertyContext.fromMap(Map("SENDGRID_API_KEY" -> api_key)), sendgridGridRequest)) {
                  case result: Result => complete(
                    StatusCodes.custom(
                      result.response.statusCode,
                      s"Request processed at [${result.response.timeStamp}] by [${result.replyFrom}]",
                      result.response.message
                    )
                  )
                  case _ => complete(StatusCodes.InternalServerError -> "Request could not be processed")
                }
              case _ => complete(StatusCodes.BadRequest -> "In correct arguments for send grid request")
            }
          }
        }
      }
    )
  }
}
