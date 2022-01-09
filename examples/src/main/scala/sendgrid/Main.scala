package sendgrid

import akka.RootManager.PropertyContext
import akka.{HealthRoute, MainRunner}
import com.typesafe.config.ConfigFactory
import org.notification.NotificationSenderFactory
import org.notification.dto.{NotifyRequest, NotifyResponse}
import sendgrid.SendGridEmailSender.SendGridEmailRequest

object Main extends App {

  val notificationFactoryBuilder = (ctx: PropertyContext) => {
    new NotificationSenderFactory {
      override def send[T <: NotifyRequest](request: T): NotifyResponse = {
        request match {
          case req: SendGridEmailRequest => SendGridEmailSender.withContext(ctx).send(req)
          case _ => throw new RuntimeException("missing implementation")
        }
      }
    }
  }

  MainRunner.run(
    ConfigFactory.empty(),
    notificationFactoryBuilder,
    HealthRoute,
    SendGridRoute
  )

}
