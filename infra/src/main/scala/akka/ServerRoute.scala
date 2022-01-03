package akka

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{complete, pathEnd, pathPrefix}
import akka.http.scaladsl.server.{Directives, Route}

object ServerRoute {
  //TODO :: Route for getting members + sending notifications ++ testing of routes
  val getRoute: Route = pathPrefix("health") {
    Directives.concat(
      pathEnd {
        Directives.concat(
          Directives.get {
            complete(StatusCodes.OK)
          }
        )
      }
    )
  }
}
