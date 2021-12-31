package integration

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.notification.NotificationSenderActor
import org.notification.NotificationSenderActor.{Notify, Result}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import sendgrid.SendGridEmailRequest

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

class SystemSpec extends TestKit(ActorSystem("test_system"))
  with ImplicitSender
  with AnyWordSpecLike
  with BeforeAndAfterAll
  with Matchers {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "NotificationSenderActor" should {
    "send email through sendgrid" in {
      val actor = system.actorOf(NotificationSenderActor.props(NotificationSenderFactoryImpl))
      actor ! Notify(SendGridEmailRequest(List.empty, List.empty, List.empty, "", "", ""))
      val result = expectMsgClass(FiniteDuration(10, TimeUnit.SECONDS), classOf[Result])
      result.response.statusCode should not be >=(400)
    }
    "send slack notification" in {}
    "send device notification" in {}
  }

}
