package sendgrid

import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers
import sendgrid.SendGridEmailSender.SendGridEmailRequest

class SendGridEmailSenderSpec extends AnyFlatSpecLike with Matchers {

  it should "send an email" in {
    val request = SendGridEmailRequest(List("satyam.sangal@knoldus.com"), List(), List(), "gaurav.srivastav1697@gmail.com", "testBody", "testSubject")
    val response = SendGridEmailSender
      .withContext(
        (_: String) => "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"
      ).send(request)
    println(response.message)
    response.statusCode shouldBe 200
    response.message shouldBe "email sent"
  }

}
