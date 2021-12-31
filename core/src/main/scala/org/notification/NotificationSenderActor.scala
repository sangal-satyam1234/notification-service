package org.notification

import akka.actor.{Actor, Props}
import akka.pattern.pipe
import org.notification.NotificationSenderActor.{NotificationSenderFactory, Notify, Result}
import org.notification.dto.{NotifyRequest, NotifyResponse}

import scala.concurrent.{ExecutionContextExecutor, Future}

object NotificationSenderActor {

  case class Notify[T <: NotifyRequest](request: T)

  case class Result(response: NotifyResponse)

  trait NotificationSenderFactory {
    def send[T <: NotifyRequest](request: T): NotifyResponse

    //class NotificationSenderFactory {
    //  def send[T: NotificationSender](request: T): NotifyResponse = {
    //    implicitly[NotificationSender[T]].send(request)
    //  }
    //}
  }

  def props(factory: NotificationSenderFactory): Props = {
    Props(new NotificationSenderActor(factory))
  }

}

class NotificationSenderActor(notificationSenderFactory: NotificationSenderFactory) extends Actor {

  implicit private val executionContext: ExecutionContextExecutor = context.system.getDispatcher

  override def receive: Receive = {
    case Notify(request) =>
      val response = Future(
        notificationSenderFactory.send(request)
      ).map(response => Result(response))
      response.pipeTo(sender())
  }

}
