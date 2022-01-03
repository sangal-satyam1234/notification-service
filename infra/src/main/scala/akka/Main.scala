package akka

import akka.Server.{Start, Stop}
import akka.actor.{ActorRef, ActorSystem, Terminated}
import akka.dispatch.MonitorableThreadFactory
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object Main extends App {

  val system: ActorSystem = ActorSystem("notification-cluster")

  val config: Config = ConfigFactory.load()
  val host = config.getString("http.ip")
  val port = config.getInt("http.port")
  val nodeId = config.getString("clustering.ip")

  val node: ActorRef = system.actorOf(RootManager.props(nodeId), "rootManager")
  val server: ActorRef = system.actorOf(Server.props(host, port), "server")

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
