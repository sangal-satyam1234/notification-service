name := "notification-service"

version := "0.1"

scalaVersion := "2.13.7"

val AkkaVersion = "2.6.18"
val AkkaHttpVersion = "10.2.7"
val SendGridVersion = "4.8.1"
val ScalaTestVersion = "3.2.10"

lazy val core = project
  .in(file("core"))

lazy val infra = project
  .in(file("infra"))
  .enablePlugins(DockerPlugin, JavaAppPackaging, AshScriptPlugin)
  .settings(
    libraryDependencies ++= Seq(
      "com.sendgrid" % "sendgrid-java" % SendGridVersion,
      "ch.qos.logback" % "logback-classic" % "1.2.10",
      "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
      "com.typesafe.akka" %% "akka-cluster-typed" % AkkaVersion,
      "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
      "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
      "io.altoo" %% "akka-kryo-serialization" % "2.3.0",
      "org.scalatest" %% "scalatest" % ScalaTestVersion % Test,
      "com.typesafe.akka" %% "akka-testkit" % AkkaVersion % Test
    ),
    Compile / mainClass := Some("akka.Main"),
    dockerBaseImage := "openjdk:11-jre-slim-buster",
    Docker / version := "latest",
    dockerExposedPorts := Seq(8000),
    dockerRepository := Some("satyam")
  )
  .dependsOn(core)

ThisBuild / resolvers += Resolver.mavenCentral
publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true)