package com.tesobe.obp

import java.util.UUID

import com.tesobe.obp.SouthKafkaStreamsActor.Topic
import com.tesobe.obp.jun2017.GetBanks
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

/**
  * Created by slavisa on 12/27/16.
  */
trait Config {

  val config = ConfigFactory.load()

  def bootstrapServers = {
    Option(config.getString("bootstrap.servers")).getOrElse {
      throw new IllegalArgumentException("Environment variable bootstrap.servers is missing. Start the application with -Dbootstrap.servers=\"localhost:9092\"")
    }
  }

  val clientId = UUID.randomUUID().toString
  val groupId = "obp-socgen" //UUID.randomUUID().toString

  val autoOffsetResetConfig = "earliest"
  val maxWakeups = 50
  val completionTimeout = FiniteDuration(config.getInt("kafka.request.processor.timeout") * 1000 - 450, MILLISECONDS)

  val targetSource = config.getString("kafka.request.target.source")
  val processorName = config.getString("kafka.request.processor.name")

  val requestTopic = config.getString("kafka.request.topic.request")
  val responseTopic = config.getString("kafka.request.topic.response")


  val topic = Topic(requestTopic, responseTopic)

  def caseClassToTopic(caseClass: Any): Topic =
    Topic("obp.Q." + caseClass.getClass.getSimpleName.replace("$", ""),
      "obp.R." + caseClass.getClass.getSimpleName.replace("$", ""))
}