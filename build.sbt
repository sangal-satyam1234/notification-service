name := "notification-service"

version := "0.1"

scalaVersion := "2.13.7"

val AkkaVersion = "2.6.18"
val AkkaHttpVersion = "10.2.7"
val SendGridVersion = "4.8.1"
val ScalaTestVersion = "3.2.10"
val LogbackVersion = "1.2.10"
val KryoVersion = "2.3.0"

lazy val core = project
  .in(file("core"))

lazy val infra = project
  .in(file("infra"))
  .settings(
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
      "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
      "com.typesafe.akka" %% "akka-cluster-typed" % AkkaVersion,
      "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
      "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
      "io.altoo" %% "akka-kryo-serialization" % KryoVersion,
      "org.scalatest" %% "scalatest" % ScalaTestVersion % Test,
      "com.typesafe.akka" %% "akka-testkit" % AkkaVersion % Test
    )
  )
  .dependsOn(core)

lazy val examples = project
  .in(file("examples"))
  .enablePlugins(DockerPlugin, JavaAppPackaging, AshScriptPlugin)
  .settings(
    libraryDependencies ++= Seq(
      "com.sendgrid" % "sendgrid-java" % SendGridVersion
    ),
    Compile / mainClass := Some("sendgrid.Main"),
    dockerBaseImage := "openjdk:11-jre-slim-buster",
    Docker / version := "latest",
    dockerExposedPorts := Seq(8000),
    dockerRepository := Some("localrepo")
  ).dependsOn(infra)

publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true)