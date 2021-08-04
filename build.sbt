val scala3Version = "3.0.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "dott-challenge",
    version := "0.1.0",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.3",
    libraryDependencies += "org.scalameta" %% "munit" % "0.7.27" % Test,
  )
