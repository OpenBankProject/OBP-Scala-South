package com.tesobe.obp

import java.util.UUID

import com.tesobe.obp.SouthKafkaStreamsActor.TopicPair
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

/**
  * Main configuration utility
  *
  */
trait Config {

  val config = ConfigFactory.load()

  def bootstrapServers = {
    Option(config.getString("bootstrap.servers")).getOrElse {
      throw new IllegalArgumentException("Environment variable bootstrap.servers is missing. Start the application with -Dbootstrap.servers=\"localhost:9092\"")
    }
  }

  val kafkaPartitions = config.getInt("kafka.partitions")
  val clientId = UUID.randomUUID().toString
  val groupId = "obp-socgen" //UUID.randomUUID().toString

  val autoOffsetResetConfig = "earliest"
  val maxWakeups = 50
  val completionTimeout = FiniteDuration(config.getInt("kafka.request.generic.timeout") * 1000 - 450, MILLISECONDS)

  val targetSource = config.getString("kafka.request.target.source")
  val processorName = config.getString("kafka.request.generic.name")

  val requestTopic = config.getString("kafka.request.topic.request")
  val responseTopic = config.getString("kafka.request.topic.response")


  val topic = TopicPair(requestTopic, responseTopic)

  val version = config.getString("kafka.version")

  def createTopicByClassName(className: String): TopicPair =
  /**
    *  eg: 
    *  obp.Jun2017.N.GetBank
    *  obp.Jun2017.S.GetBank
    */
    TopicPair(
      s"obp.${version}.N." + className.replace("$", ""),
      s"obp.${version}.S." + className.replace("$", "")
    )
}