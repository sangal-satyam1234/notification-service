name := "notification-service"

version := "0.1"

scalaVersion := "2.13.7"

val AkkaVersion = "2.6.18"
val AkkaHttpVersion = "10.2.7"
val SendGridVersion = "4.8.1"
val ScalaTestVersion = "3.2.10"

lazy val core = project
  .in(file("core"))
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion
    )
  )

lazy val infra = project
  .in(file("infra"))
  .enablePlugins(DockerPlugin, JavaAppPackaging, AshScriptPlugin)
  .settings(
    libraryDependencies ++= Seq(
      "com.sendgrid" % "sendgrid-java" % SendGridVersion,
      "ch.qos.logback" % "logback-classic" % "1.2.9",
      "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
      "com.typesafe.akka" %% "akka-cluster-typed" % AkkaVersion,
      "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
      "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
      "org.scalatest" %% "scalatest" % ScalaTestVersion % Test,
      "com.typesafe.akka" %% "akka-testkit" % AkkaVersion % Test
    ),
    mainClass in Compile := Some("akka.Main"),
    dockerBaseImage := "java:8-jre-alpine",
    version in Docker := "latest",
    dockerExposedPorts := Seq(8000),
    dockerRepository := Some("satyam")
  )
  .dependsOn(core)

publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true)