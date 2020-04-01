name := "mmt"

version := "0.1"

scalaVersion := "2.12.9"

libraryDependencies += "mmt" % "mmt" % "19.0.0" from "https://github.com/UniFormal/MMT/releases/download/19.0.0/mmt.jar"

libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.1" % "test"
