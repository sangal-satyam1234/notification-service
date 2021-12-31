package org.notification.sender

import org.notification.dto.{NotifyRequest, NotifyResponse}

trait NotificationSender[T <: NotifyRequest] {
  def send(request: T): NotifyResponse
}

/*
trait EmailSender extends NotificationSender[SendEmailRequest] {
  override def send(request: SendEmailRequest): NotifyResponse = sendEmail(request)

  def sendEmail(request: SendEmailRequest): SendEmailResponse
}
 */
