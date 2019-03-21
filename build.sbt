import sbt.Keys.libraryDependencies

name := "kafka-streams-example"

version := "1.0"

scalaVersion := "2.12.8"

scalacOptions := Seq("-unchecked", "-deprecation")

resolvers += DefaultMavenRepository

lazy val commonSettings = Seq(
  organization := "com.luke",
  scalaVersion := "2.12.8",
  libraryDependencies ++= Seq(
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
  )
)

lazy val kafkarouter = project.in(file("kafka-router"))
  .settings(commonSettings: _*)
  .settings(
    name := "kafka-router",
    libraryDependencies ++= Seq(
      "org.apache.kafka" % "kafka-clients" % "2.1.1",
      "org.apache.kafka" %% "kafka-streams-scala" % "2.1.1",
      "ch.qos.logback" % "logback-classic" % "1.0.13",
    )
  )


lazy val root = project.in(file("."))
  .aggregate(kafkarouter)
  .settings(commonSettings: _*)
