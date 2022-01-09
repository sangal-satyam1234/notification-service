package org.notification.sender

import org.notification.dto.{NotifyRequest, NotifyResponse}

trait NotificationSender[T <: NotifyRequest] {
  def send(request: T): NotifyResponse
}

