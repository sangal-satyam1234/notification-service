import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.notification.NotificationSenderActor
import org.notification.NotificationSenderActor.{NotificationSenderFactory, Notify, Result}
import org.notification.dto.{NotifyRequest, NotifyResponse}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import sendgrid.{SendGridEmailRequest, SendGridEmailSender}

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

class SendGridEmailSenderISpec extends TestKit(ActorSystem("SendGridEmailSender"))
  with ImplicitSender
  with AnyWordSpecLike
  with BeforeAndAfterAll
  with Matchers {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "NotificationSenderActor" should {
    "send email through sendgrid" in {
      val factory: NotificationSenderFactory = new NotificationSenderFactory {
        override def send[T <: NotifyRequest](request: T): NotifyResponse = SendGridEmailSender.send(request.asInstanceOf[SendGridEmailRequest])
      }
      val actor = system.actorOf(NotificationSenderActor.props(factory))
      actor ! Notify(SendGridEmailRequest(List.empty, List.empty, List.empty, "", "", ""))
      val result = expectMsgClass(FiniteDuration(10, TimeUnit.SECONDS), classOf[Result])
      result.response.statusCode shouldBe 401
    }
  }

}
