name := "OBP-Adapter_Leumi"

version := "1.0"

organization := "com.tesobe"

scalaVersion := "2.12.2"

resolvers ++= Seq(
  Resolver.bintrayRepo("hseeberger", "maven"),
  Resolver.sonatypeRepo("public"),
  Resolver.sonatypeRepo("snapshots"),
  DefaultMavenRepository
)

libraryDependencies ++= {
  val akkaV = "2.4.16"
  val akkaKafkaV = "0.16"
  val akkaHttpV = "10.0.4"
  val avro4sV = "1.6.4"
  val avroV = "1.8.1"
  val kafkaV = "0.10.0.1"
  val logbackVersion = "1.1.3"
  val scalaLoggingVersion = "3.5.0"
  val circeVersion = "0.7.0"
  val kamonVersion = "0.6.6"

  Seq(
    "com.typesafe.akka"                   %% "akka-slf4j"             % akkaV,
    "com.typesafe.akka"                   %% "akka-stream-kafka"          % akkaKafkaV,
    "com.typesafe.akka"                   %% "akka-http"                  % "10.0.9",
    "com.typesafe.akka"                   %% "akka-http-testkit"          % "10.0.9" % Test,
    "com.sksamuel.avro4s"                 %% "avro4s-core"                % avro4sV,
    "org.apache.avro"                      % "avro"                       % avroV,
    "ch.qos.logback"                       % "logback-classic"            % logbackVersion,
    "io.circe"                             % "circe-core_2.12"            % circeVersion,
    "io.circe"                             % "circe-parser_2.12"          % circeVersion,
    "io.circe"                             % "circe-generic_2.12"         % circeVersion,
    "de.knutwalker"                       %% "akka-stream-circe"           % "3.3.0",
    "de.knutwalker"                       %% "akka-stream-json"           % "3.3.0",
    "de.knutwalker"                       %% "akka-http-json"             % "3.3.0",
    "org.apache.camel"                     % "camel-stream"               % "2.19.0",
    "org.scalatest"                       %% "scalatest"                  % "3.0.1",
    "net.liftweb"                         %% "lift-json"                  % "3.1.0-M3",
    "net.liftweb"                         %% "lift-util"                  % "3.1.0-M3", 
    "co.fs2" %% "fs2-core" % "0.9.6",
    "co.fs2" %% "fs2-io" % "0.9.6",
    "com.typesafe.scala-logging"           % "scala-logging_2.12"         % scalaLoggingVersion,
    "org.apache.httpcomponents"            % "httpclient"                 % "4.5.3",
    "org.mock-server"                      % "mockserver-netty"           % "3.10.8",
    "net.manub"                           %% "scalatest-embedded-kafka-streams" % "0.14.0",
    "com.typesafe.play"                   %% "play-ahc-ws-standalone"     % "1.0.0-M10"
  )
}

com.github.retronym.SbtOneJar.oneJarSettings
