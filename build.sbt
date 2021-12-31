name := "notification-service"

version := "0.1"

scalaVersion := "2.13.7"

val AkkaVersion = "2.6.18"

lazy val core = project
  .in(file("core"))
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion
    )
  )

lazy val infra = project
  .in(file("infra"))
  .settings(
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.10" % Test,
      "com.typesafe.akka" %% "akka-testkit" % AkkaVersion % Test
    )
  )
  .dependsOn(core)