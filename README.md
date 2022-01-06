This repo provides a simple notification reactive infrastructure which can serve as single integration point for different notification providers. The client need not to be worried about scaling or fault tolerance and it should be easy to integrate additional service providers.


# **Building**
     sbt clean compile docker:publishLocal

# **Running**
    docker-compose up -d

# **APIs**
1) GET /health : Check if service is up
2) GET /health/nodes : Check all nodes in cluster
3) POST /notify/email/sendgrid : Send an email via sendgrid
   
        Header : SENDGRID_API_KEY = <key>
        BODY : {
            "recipients" : ["t.com"],
            "cc" : ["c.com"],
            "bcc" : ["bc.com"],
            "sender" : "sender.com",
            "htmlBody" : "body",
            "htmlTitle" : "title"
            }

# **Development**
1) Adding new notification providers
   
      a) Make a new request body (SendGridEmailRequest.scala).
   
      b) Provide implementation for provider (SendGridEmailSender.scala).
   
      c) Add route to your request (ServerRoute.scala#NotificationRoute).

      d) Wire your provider in implementation using builder (Main.scala#FactoryBuilder).

2) Https is supported. Enable it in application.conf file.

References:
1) https://www.freecodecamp.org/news/how-to-make-a-simple-application-with-akka-cluster-506e20a725cf/
2) https://github.com/mel3kings/scalable-email-service
3) https://lightbend.github.io/ssl-config/CertificateGeneration.html

Features to add
1) blacklist/whitelist recipients/senders
2) logging events to stream
3) automated end 2 end tests