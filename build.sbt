name := "notification-service"

version := "0.1"

scalaVersion := "2.13.7"

lazy val core = project
  .in(file("core"))
  .settings(
    libraryDependencies ++= Seq(

    )
  )

lazy val infra = project
  .in(file("infra"))
  .settings(
    libraryDependencies ++= Seq(

    )
  )