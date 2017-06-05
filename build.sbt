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

  Seq(
    "com.typesafe.akka"                   %% "akka-slf4j"             % akkaV,
    "com.typesafe.akka"                   %% "akka-stream-kafka"          % akkaKafkaV,
    "com.sksamuel.avro4s"                 %% "avro4s-core"                % avro4sV,
    "org.apache.avro"                      % "avro"                       % avroV,
    "ch.qos.logback"                       % "logback-classic"            % logbackVersion,
    "com.typesafe.akka"                   %% "akka-http-spray-json"       % akkaHttpV,
    "com.typesafe.scala-logging"           % "scala-logging_2.12"         % scalaLoggingVersion
  )
}

com.github.retronym.SbtOneJar.oneJarSettings
