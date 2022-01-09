package akka

import akka.RootManager.GetClusterMembers
import akka.actor.ActorRef
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout
import spray.json.DefaultJsonProtocol.StringJsonFormat
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import java.time.Duration

trait ServerRoute extends SprayJsonSupport {
  def buildRoute: ActorRef => Route
}

object HealthRoute extends ServerRoute {

  override def buildRoute: ActorRef => Route = rootActor => {
    pathPrefix("health") {
      concat(
        pathEnd {
          concat(
            get {
              complete(StatusCodes.OK)
            }
          )
        },
        path("members") {
          concat(
            get {
              implicit val timeout: Timeout = Timeout.create(Duration.ofSeconds(3))
              implicit val listOfString: RootJsonFormat[List[String]] = DefaultJsonProtocol.listFormat[String]
              val members = (rootActor ? GetClusterMembers).mapTo[List[String]]
              onSuccess(members) {
                case members: List[String] => complete(members)
                case _ => complete(StatusCodes.InternalServerError -> "No cluster nodes could be found")
              }
            }
          )
        }
      )
    }
  }
}
