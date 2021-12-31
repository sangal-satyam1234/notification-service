package org.notification.dto

trait NotifyResponse {
  val statusCode: Int
  val message: String
  val timeStamp: String
}

/*
case class SendEmailResponse(
                              statusCode: Int,
                              message: String,
                              timeStamp: String
                            ) extends NotifyResponse
 */