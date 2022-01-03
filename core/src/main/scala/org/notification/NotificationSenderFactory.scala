package org.notification

import org.notification.dto.{NotifyRequest, NotifyResponse}

trait NotificationSenderFactory {
  def send[T <: NotifyRequest](request: T): NotifyResponse
}
