package akka

import akka.RootManager.PropertyContext
import akka.Server.{Start, Stop}
import akka.actor.{ActorRef, ActorSystem, Terminated}
import akka.dispatch.MonitorableThreadFactory
import akka.http.scaladsl.server.{Directives, Route}
import com.typesafe.config.{Config, ConfigFactory}
import org.notification.NotificationSenderFactory

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object MainRunner {

  def run(
           applicationConfig: Config,
           notificationFactoryBuilder: PropertyContext => NotificationSenderFactory,
           serverRoutes: ServerRoute*
         ): Unit = {
    val config = applicationConfig.withFallback(ConfigFactory.load())
    val nodeId = config.getString("clustering.ip")
    val serverConfig = config.getConfig("server")
    val system: ActorSystem = ActorSystem("notification-cluster", config)
    val node: ActorRef = system.actorOf(RootManager.props(nodeId, notificationFactoryBuilder), "rootManager")
    val routes = serverRoutes.map(builder => builder.buildRoute(node))
    val mainRoute: Route = Directives.concat(routes: _*)
    val server = system.actorOf(Server.props(serverConfig, mainRoute), "server")
    server ! Start

    Runtime.getRuntime.addShutdownHook(
      MonitorableThreadFactory(
        "monitoring-thread-factory",
        daemonic = false,
        Some(Thread.currentThread().getContextClassLoader)
      ).newThread(() => {
        server ! Stop
        val terminate: Future[Terminated] = system.terminate()
        Await.result(terminate, Duration("10 seconds"))
        ()
      })
    )
  }

}
