package akka

import akka.Server.{Start, StartFailed, Started, Stop}
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.{ConnectionContext, Http, HttpsConnectionContext}
import akka.pattern.pipe
import com.typesafe.config.Config

import java.io.{BufferedInputStream, File, FileInputStream}
import java.security.{KeyStore, SecureRandom}
import javax.net.ssl.{KeyManagerFactory, SSLContext, TrustManagerFactory}
import scala.concurrent.{ExecutionContextExecutor, Future}

object Server {

  sealed trait Message

  case object Start extends Message

  private final case class StartFailed(cause: Throwable) extends Message

  private final case class Started(binding: ServerBinding) extends Message

  case object Stop extends Message

  def props(config: Config, route: Route): Props = Props(new Server(config, route))
}

class Server(serverConfig: Config, route: Route) extends Actor with ActorLogging {

  implicit val system: ActorSystem = context.system
  implicit val dispatcher: ExecutionContextExecutor = context.system.getDispatcher

  def serverBinding: Future[Http.ServerBinding] = {
    val host = serverConfig.getString("ip")
    val port = serverConfig.getInt("port")
    val sslEnabled = serverConfig.getBoolean("ssl.enabled")
    if (sslEnabled) {
      val password = serverConfig.getString("ssl.keystore.password").toCharArray
      val keystoreType = serverConfig.getString("ssl.keystore.type")
      val keystorePath = serverConfig.getString("ssl.keystore.path")
      val manager = serverConfig.getString("ssl.keystore.manager")
      val https = httpsConnectionContext(keystoreType, keystorePath, manager, password)
      Http().newServerAt(host, port).enableHttps(https).bind(route)
    }
    else Http().newServerAt(host, port).bind(route)
  }

  override def receive: Receive = {
    case Start =>
      context.become(starting(wasStopped = false))
      serverBinding.map({
        case binding => Started(binding)
        case _ => StartFailed(_)
      }).pipeTo(self)
  }

  def starting(wasStopped: Boolean): Receive = {
    case StartFailed(cause) => throw new Exception("Server failed to start", cause)
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

  private def httpsConnectionContext(keystoreType: String, keystorePath: String, manager: String, password: Array[Char]): HttpsConnectionContext = {
    val ks: KeyStore = KeyStore.getInstance(keystoreType)
    val keystore = new BufferedInputStream(new FileInputStream(new File(keystorePath)))
    require(keystore != null, "Keystore required!")
    ks.load(keystore, password)
    val keyManagerFactory: KeyManagerFactory = KeyManagerFactory.getInstance(manager)
    keyManagerFactory.init(ks, password)
    val tmf: TrustManagerFactory = TrustManagerFactory.getInstance(manager)
    tmf.init(ks)
    val sslContext: SSLContext = SSLContext.getInstance("TLS")
    sslContext.init(keyManagerFactory.getKeyManagers, tmf.getTrustManagers, new SecureRandom)
    ConnectionContext.httpsServer(sslContext)
  }

}
