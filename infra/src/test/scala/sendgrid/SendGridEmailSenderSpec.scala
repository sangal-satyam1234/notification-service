package sendgrid

import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers
import sendgrid.SendGridEmailSender.SendGridEmailRequest

class SendGridEmailSenderSpec extends AnyFlatSpecLike with Matchers {

  it should "send an email" in {
    val request = SendGridEmailRequest(List(""), List(), List(), "", "testBody", "testSubject")
    val response = SendGridEmailSender
      .withContext(
        (_: String) => Some("insert key")
      ).send(request)
    println(response.message)
    response.statusCode shouldBe 200
    response.message shouldBe "email sent"
  }

}
