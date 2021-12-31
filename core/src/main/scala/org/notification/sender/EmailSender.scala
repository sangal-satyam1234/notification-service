package org.notification.sender

import org.notification.dto.{SendEmailRequest, SendEmailResponse}

trait EmailSender {
  def sendEmail(request: SendEmailRequest): SendEmailResponse
}
