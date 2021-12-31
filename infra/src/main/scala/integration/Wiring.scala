package integration

import akka.actor.{ActorRef, ActorSystem, Terminated}
import akka.dispatch.MonitorableThreadFactory
import com.typesafe.config.ConfigFactory
import org.notification.NotificationSenderActor

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object Wiring extends App {
  val system: ActorSystem = ActorSystem("notification_sender", ConfigFactory.load())
  val sender: ActorRef = system.actorOf(NotificationSenderActor.props(NotificationSenderFactoryImpl))

  //todo :: expose api's


  Runtime.getRuntime.addShutdownHook(
    MonitorableThreadFactory(
      "monitoring-thread-factory",
      daemonic = false,
      Some(Thread.currentThread().getContextClassLoader)
    ).newThread(() => {
      val terminate: Future[Terminated] = system.terminate()
      Await.result(terminate, Duration("3 seconds"))
      ()
    })
  )

}
