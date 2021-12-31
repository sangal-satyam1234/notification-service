name := "notification-service"

version := "0.1"

scalaVersion := "2.13.7"

val AkkaVersion = "2.6.18"
val SendGridVersion = "4.8.1"
val ScalaTestVersion = "3.2.10"

lazy val core = project
  .in(file("core"))
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion
    )
  )

lazy val api = project
  .in(file("api"))
  .settings(
    libraryDependencies ++= Seq(

    )
  )

lazy val infra = project
  .in(file("infra"))
  .settings(
    libraryDependencies ++= Seq(
      "com.sendgrid" % "sendgrid-java" % SendGridVersion,
      "ch.qos.logback" % "logback-classic" % "1.2.9",
      "org.scalatest" %% "scalatest" % ScalaTestVersion % Test,
      "com.typesafe.akka" %% "akka-testkit" % AkkaVersion % Test
    )
  )
  .dependsOn(core)