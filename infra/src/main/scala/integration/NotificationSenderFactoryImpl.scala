package integration

import org.notification.NotificationSenderActor.NotificationSenderFactory
import org.notification.dto.{NotifyRequest, NotifyResponse}
import sendgrid.{SendGridEmailRequest, SendGridEmailSender}


object NotificationSenderFactoryImpl extends NotificationSenderFactory {

  //todo :: use implicit pattern factory
  override def send[T <: NotifyRequest](request: T): NotifyResponse = {
    request match {
      case req: SendGridEmailRequest => SendGridEmailSender.send(req)
      case _ => throw new RuntimeException("missing implementation")
    }
  }

  //  def sendImplicit[T](request: T)(implicit notificationSender: NotificationSender[T]): NotifyResponse = {
  //    notificationSender.send(request)
  //  }

}
