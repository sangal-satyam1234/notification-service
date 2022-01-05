This repo provides a simple notification reactive infrastructure 
using akka-clustering and akka-http so that users can easily wire their own notification providers and functionality.


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


Features to add
1) blacklist/whitelist recipients/senders
2) logging events to stream
3) security to prevent misuse of service - done partially
4) automated end 2 end tests