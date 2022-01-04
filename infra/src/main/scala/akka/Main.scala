package akka

import akka.RootManager.PropertyContext
import akka.Server.{Start, Stop}
import akka.actor.{ActorRef, ActorSystem, Terminated}
import akka.dispatch.MonitorableThreadFactory
import akka.http.scaladsl.server.Route
import com.typesafe.config.{Config, ConfigFactory}
import org.notification.NotificationSenderFactory
import org.notification.dto.{NotifyRequest, NotifyResponse}
import sendgrid.SendGridEmailSender
import sendgrid.SendGridEmailSender.SendGridEmailRequest

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object Main extends App {

  val config: Config = ConfigFactory.load()
  val host = config.getString("http.ip")
  val port = config.getInt("http.port")
  val nodeId = config.getString("clustering.ip")

  val factoryBuilder = (ctx: PropertyContext) => {
    new NotificationSenderFactory {
      //todo :: use implicit pattern factory
      override def send[T <: NotifyRequest](request: T): NotifyResponse = {
        request match {
          case req: SendGridEmailRequest => SendGridEmailSender.withContext(ctx).send(req)
          case _ => throw new RuntimeException("missing implementation")
        }
      }
    }
  }

  val system: ActorSystem = ActorSystem("notification-cluster")
  val node: ActorRef = system.actorOf(RootManager.props(nodeId, factoryBuilder), "rootManager")
  val route: Route = new ServerRoute(node).getRoute
  val server: ActorRef = system.actorOf(Server.props(host, port, route), "server")

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
