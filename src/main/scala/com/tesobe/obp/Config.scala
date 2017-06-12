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
  val completionTimeout = FiniteDuration(config.getInt("kafka.request.generic.timeout") * 1000 - 450, MILLISECONDS)

  val targetSource = config.getString("kafka.request.target.source")
  val processorName = config.getString("kafka.request.generic.name")

  val requestTopic = config.getString("kafka.request.topic.request")
  val responseTopic = config.getString("kafka.request.topic.response")


  val topic = Topic(requestTopic, responseTopic)

  /**
    * Helper for getting kafka topics from case classes.
    *
    * @param caseClass
    * @return pair of strings representing kafka topics
    * Note the form:
    * <li> "obp" - just as an ID</li>
    * <li> "Q" or "R" which stands from Query/Response </li>
    * <li> case class simple name without "$" because it is invalid char for kafka topic name</li>
    */
  def caseClassToTopic(caseClass: Any): Topic =
    Topic("obp.Q." + caseClass.getClass.getSimpleName.replace("$", ""),
      "obp.R." + caseClass.getClass.getSimpleName.replace("$", ""))
}