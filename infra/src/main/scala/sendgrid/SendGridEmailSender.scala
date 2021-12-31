package sendgrid

import com.sendgrid.helpers.mail.Mail
import com.sendgrid.helpers.mail.objects.{Content, Email, Personalization}
import com.sendgrid.{Method, Request, Response, SendGrid}
import org.notification.dto.{NotifyRequest, NotifyResponse}
import org.notification.sender.NotificationSender

import scala.util.Try

case class SendGridEmailRequest(
                                 recipients: List[String],
                                 cc: List[String],
                                 bcc: List[String],
                                 sender: String,
                                 htmlBody: String,
                                 htmlTitle: String
                               ) extends NotifyRequest

case class SendGridEmailResponse(statusCode: Int, message: String, timeStamp: String = System.currentTimeMillis().toString) extends NotifyResponse

object SendGridEmailSender extends NotificationSender[SendGridEmailRequest] {

  override def send(request: SendGridEmailRequest): NotifyResponse = {
    val mail = constructMail(request)
    val sendGridResponse = Try(process(mail))
    //TODO :: Error handling here
    sendGridResponse.map(constructResponse).getOrElse(
      SendGridEmailResponse(500, message = "Something went wrong with sendgrid")
    )
  }

  private def process(mail: Mail): Response = {
    val sg = new SendGrid(System.getenv("SENDGRID_API_KEY"))
    val request = new Request
    request.setMethod(Method.POST)
    request.setEndpoint("mail/send")
    request.setBody(mail.build())
    sg.api(request)
  }

  private def constructMail(emailRequest: SendGridEmailRequest): Mail = {
    val mail = new Mail()
    val p = new Personalization
    emailRequest.recipients.foreach(email => p.addTo(new Email(email)))
    emailRequest.cc.foreach(cc => p.addCc(new Email(cc)))
    emailRequest.bcc.foreach(bcc => p.addBcc(new Email(bcc)))
    mail.setSubject(emailRequest.htmlTitle)
    mail.addContent(new Content("text/plain", emailRequest.htmlBody))
    mail.addPersonalization(p)
    mail.setFrom(new Email(emailRequest.sender))
    mail
  }

  private def constructResponse(response: Response): NotifyResponse = {
    SendGridEmailResponse(response.getStatusCode, response.getBody)
  }

}
