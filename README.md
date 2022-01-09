This repo provides a simple notification reactive infrastructure which can serve as single integration point for different notification providers. The client need not to be worried about scaling or fault tolerance and it should be easy to integrate additional service providers.

There is a sample example to reference at ./examples

# **Building**
     sbt clean compile docker:publishLocal

# **Running**
    docker-compose up -d

# **APIs**
1) GET /health : Check if service is up
2) GET /health/members : Check all nodes in cluster
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

# **Notes**
1) Adding new notification providers
   
      a) Make a new request body (SendGridEmailRequest.scala).
   
      b) Provide implementation for provider (SendGridEmailSender.scala).
   
      c) Add route to your request (SendGridRoute.scala).

      d) Use MainRunner ( sendgrid.Main.scala). This starts the actor system and attaches shutdown hook.

2) Https is supported. There is a default fallback configuration (application.conf) .

References:
1) https://www.freecodecamp.org/news/how-to-make-a-simple-application-with-akka-cluster-506e20a725cf/
2) https://github.com/mel3kings/scalable-email-service
3) https://lightbend.github.io/ssl-config/CertificateGeneration.html

Features to add
1) Encrypting notification messages internally. 
   
   User can do it externally via proxies as workaround.
2) Logging notification events to external stream.
   
   User should be able to attach his own streaming endpoints.
3) Automated end 2 end testing infra.

   User should be able to test the system.
