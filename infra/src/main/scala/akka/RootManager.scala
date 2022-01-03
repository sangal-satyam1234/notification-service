package akka

import akka.RootManager.{GetClusterMembers, Notify, getFactory}
import akka.actor.{Actor, ActorRef, Props}
import akka.cluster.ClusterManager
import akka.cluster.ClusterManager.GetMembers
import akka.routing.FromConfig
import org.notification.NotificationSenderFactory
import org.notification.dto.{NotifyRequest, NotifyResponse}
import sendgrid.{SendGridEmailRequest, SendGridEmailSender}

object RootManager {

  case class Notify(request: NotifyRequest)

  case object GetClusterMembers

  def getFactory: NotificationSenderFactory = new NotificationSenderFactory {
    //todo :: use implicit pattern factory
    override def send[T <: NotifyRequest](request: T): NotifyResponse = {
      request match {
        case req: SendGridEmailRequest => SendGridEmailSender.send(req)
        case _ => throw new RuntimeException("missing implementation")
      }
    }
  }

  def props(nodeId: String): Props = Props(new RootManager(nodeId))
}

class RootManager(nodeId: String) extends Actor {

  val notificationManager: ActorRef = context.actorOf(NotificationManager.props(nodeId, getFactory), "notificationManager")
  val notificationRouter: ActorRef = context.actorOf(FromConfig.props(Props.empty), "notificationRouter")
  val clusterManager: ActorRef = context.actorOf(ClusterManager.props(nodeId), "clusterManager")

  override def receive: Receive = {
    case GetClusterMembers => clusterManager forward GetMembers
    case Notify(value) => notificationRouter forward NotificationManager.Notify(value)
  }

}