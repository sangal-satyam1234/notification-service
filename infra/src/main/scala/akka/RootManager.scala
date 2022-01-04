package akka

import akka.RootManager.{GetClusterMembers, Notify, PropertyContext}
import akka.actor.{Actor, ActorRef, Props}
import akka.cluster.ClusterManager
import akka.cluster.ClusterManager.GetMembers
import akka.routing.FromConfig
import org.notification.NotificationSenderFactory
import org.notification.dto.NotifyRequest

object RootManager {

  case class Notify(context: PropertyContext, request: NotifyRequest)

  case object GetClusterMembers

  trait PropertyContext {
    def getProperty(key: String): Any
  }

  def props(nodeId: String, factoryBuilder: Function[PropertyContext, NotificationSenderFactory]): Props = Props(new RootManager(nodeId, factoryBuilder))

}

class RootManager(nodeId: String, factoryBuilder: PropertyContext => NotificationSenderFactory) extends Actor {

  val notificationManager: ActorRef = context.actorOf(NotificationManager.props(nodeId, factoryBuilder), "notificationManager")
  val notificationRouter: ActorRef = context.actorOf(FromConfig.props(Props.empty), "notificationRouter")
  val clusterManager: ActorRef = context.actorOf(ClusterManager.props(nodeId), "clusterManager")

  override def receive: Receive = {
    case GetClusterMembers => clusterManager forward GetMembers
    case Notify(ctx, value) => notificationRouter forward NotificationManager.Notify(ctx, value)
  }

}