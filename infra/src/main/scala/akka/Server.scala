package akka

import akka.Server.{Start, StartFailed, Started, Stop}
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Route

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

object Server {

  sealed trait Message

  case object Start extends Message

  private final case class StartFailed(cause: Throwable) extends Message

  private final case class Started(binding: ServerBinding) extends Message

  case object Stop extends Message

  def props(host: String, port: Int): Props = Props(new Server(host, port))
}

class Server(host: String, port: Int) extends Actor with ActorLogging {

  implicit val system: ActorSystem = context.system
  implicit val dispatcher: ExecutionContextExecutor = context.system.getDispatcher

  val route: Route = ServerRoute.getRoute

  def serverBinding: Future[Http.ServerBinding] = Http().newServerAt(host, port).bind(route)

  override def receive: Receive = {
    case Start =>
      context.become(starting(wasStopped = false))
      serverBinding.onComplete {
        case Success(binding) => self ! Started(binding)
        case Failure(exception) => self ! StartFailed(exception)
      }
    case _ => log.warning("Server is offline")
  }

  def starting(wasStopped: Boolean): Receive = {
    case StartFailed(cause) => throw new RuntimeException("Server failed to start", cause)
    case Started(binding) =>
      log.info("Server online at http://{}:{}/",
        binding.localAddress.getHostString,
        binding.localAddress.getPort)
      if (wasStopped) self ! Stop
      context.become(running(binding))
    case Stop => context.become(starting(wasStopped = true))
  }

  def running(binding: ServerBinding): Receive = {
    case Stop =>
      log.info(
        "Stopping server http://{}:{}/",
        binding.localAddress.getHostString,
        binding.localAddress.getPort)
      binding.unbind()
      context.stop(self)
    case Start => log.info("Server is already online at http://{}:{}/",
      binding.localAddress.getHostString,
      binding.localAddress.getPort)
  }

}
