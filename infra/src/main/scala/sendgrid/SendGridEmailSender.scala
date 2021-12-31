package sendgrid

import org.notification.dto.{NotifyRequest, NotifyResponse}
import org.notification.sender.NotificationSender

case class SendGridEmailRequest(
                             recipients: List[String],
                             cc: List[String],
                             bcc: List[String],
                             sender: String,
                             htmlBody: String,
                             htmlTitle: String
                           ) extends NotifyRequest

object SendGridEmailSender extends NotificationSender[SendGridEmailRequest] {
  override def send(request: SendGridEmailRequest): NotifyResponse = {
    //todo:: integration with sendgrid
    new NotifyResponse {
      override val statusCode: Int = 400
      override val message: String = "not implemented"
      override val timeStamp: String = System.currentTimeMillis().toString
    }
  }
}
