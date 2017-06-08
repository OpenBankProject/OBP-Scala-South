name := "OBP-Scala-South"

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

  Seq(
    "com.typesafe.akka"                   %% "akka-slf4j"             % akkaV,
    "com.typesafe.akka"                   %% "akka-stream-kafka"          % akkaKafkaV,
    "com.sksamuel.avro4s"                 %% "avro4s-core"                % avro4sV,
    "org.apache.avro"                      % "avro"                       % avroV,
    "ch.qos.logback"                       % "logback-classic"            % logbackVersion,
    "com.typesafe.akka"                   %% "akka-http-spray-json"       % akkaHttpV,
    "io.circe"                             % "circe-core_2.12"            % circeVersion,
    "io.circe"                             % "circe-parser_2.12"          % circeVersion,
    "io.circe"                             % "circe-generic_2.12"         % circeVersion,
    "de.knutwalker"                       %% "akka-stream-circe"           % "3.3.0",
    "de.knutwalker"                       %% "akka-stream-json"           % "3.3.0",
    "de.knutwalker"                       %% "akka-http-json"             % "3.3.0",
    "org.apache.camel"                     % "camel-stream"               % "2.19.0",
  "co.fs2" %% "fs2-core" % "0.9.6",
  "co.fs2" %% "fs2-io" % "0.9.6",
  "com.typesafe.scala-logging"           % "scala-logging_2.12"         % scalaLoggingVersion
  )
}

com.github.retronym.SbtOneJar.oneJarSettings
