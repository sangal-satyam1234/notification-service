package org.notification

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern.pipe
import org.notification.NotificationSenderActor.{Notify, Result}
import org.notification.dto.{NotifyRequest, NotifyResponse}

import scala.concurrent.{ExecutionContextExecutor, Future}

object NotificationSenderActor {

  case class Notify[T <: NotifyRequest](request: T, replyTo: ActorRef)

  case class Result(replyFrom:String,response: NotifyResponse)

  def props(id:String,factory: NotificationSenderFactory): Props = {
    Props(new NotificationSenderActor(id,factory))
  }

}

class NotificationSenderActor(id:String,notificationSenderFactory: NotificationSenderFactory) extends Actor with ActorLogging {

  implicit private val executionContext: ExecutionContextExecutor = context.system.getDispatcher

  override def receive: Receive = {
    case Notify(request, replyTo) =>
      log.info(s"Received request [$request] at [$id]")
      val response = Future(
        notificationSenderFactory.send(request)
      ).map(response => {
        log.info(s"Response from provider [$response]")
        Result(id,response)
      })
      response.pipeTo(replyTo)
  }

}
