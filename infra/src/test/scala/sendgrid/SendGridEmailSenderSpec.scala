package sendgrid

import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

class SendGridEmailSenderSpec extends AnyFlatSpecLike with Matchers {

  it should "send an email" in {
    val request = SendGridEmailRequest(List.empty, List.empty, List.empty, "", "", "")
    val response = SendGridEmailSender.send(request)
    response.statusCode shouldBe 200
    response.message shouldBe "email sent"
  }

}
