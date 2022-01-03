package akka

import akka.NotificationManager.Notify
import akka.actor.{Actor, ActorRef, Props}
import org.notification.dto.NotifyRequest
import org.notification.{NotificationSenderActor, NotificationSenderFactory}

object NotificationManager {

  case class Notify[T <: NotifyRequest](request: T)

  def props(id: String, factory: NotificationSenderFactory): Props = Props(new NotificationManager(id, factory))

}

class NotificationManager(id: String, factory: NotificationSenderFactory) extends Actor {

  val worker: ActorRef = context.actorOf(NotificationSenderActor.props(id, factory))

  override def receive: Receive = {
    case Notify(request) =>
      worker ! NotificationSenderActor.Notify(request, sender())
  }

}
