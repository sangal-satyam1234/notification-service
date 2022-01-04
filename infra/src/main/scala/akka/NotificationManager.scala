package akka

import akka.NotificationManager.{Notify, Result}
import akka.RootManager.PropertyContext
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props, SupervisorStrategy}
import akka.pattern.pipe
import org.notification.NotificationSenderFactory
import org.notification.dto.{NotifyRequest, NotifyResponse}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContextExecutor, Future}

object NotificationManager {

  case class Notify[T <: NotifyRequest](context: PropertyContext, request: T) extends KryoSerializable

  case class Result(replyFrom: String, response: NotifyResponse) extends KryoSerializable

  def props(id: String, factoryBuilder: PropertyContext => NotificationSenderFactory): Props = Props(new NotificationManager(id, factoryBuilder))

}

class NotificationManager(id: String, builder: Function[PropertyContext, NotificationSenderFactory]) extends Actor with ActorLogging {

  implicit private val executionContext: ExecutionContextExecutor = context.system.getDispatcher

  override def receive: Receive = {
    case Notify(ctx, request) =>
      val notificationSenderFactory = builder(ctx)
      log.info(s"Received request [$request] at [$id]")
      val response = Future(
        notificationSenderFactory.send(request)
      ).map(response => {
        log.info(s"Response from provider [$response]")
        Result(id, response)
      })
      response.pipeTo(sender())
  }

  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy(
    maxNrOfRetries = 5,
    withinTimeRange = 20.seconds,
    loggingEnabled = true
  ) {
    case _ => SupervisorStrategy.Restart
  }

}
