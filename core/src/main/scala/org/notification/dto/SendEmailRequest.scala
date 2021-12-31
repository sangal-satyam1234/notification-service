package org.notification.dto

//todo :: add validations
case class SendEmailRequest(
                             recipients: List[String],
                             cc: List[String],
                             bcc: List[String],
                             sender: String,
                             htmlBody: String,
                             htmlTitle: String
                           )
