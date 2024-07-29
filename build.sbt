
ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.19"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % Test

lazy val root = (project in file("."))
  .settings(
    name := "play-template"
  )
  .enablePlugins(PlayScala)