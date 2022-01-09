package org.notification.dto

trait NotifyResponse {
  val statusCode: Int
  val message: String
  val timeStamp: String
}