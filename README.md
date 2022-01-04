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


Features to add
1) blacklist/whitelist recipients
2) logging events to stream
3) akka clustering - done
4) containerized deployment - done
5) security to prevent misuse of service - done partially
6) automated tests
7) Exceptional Handling