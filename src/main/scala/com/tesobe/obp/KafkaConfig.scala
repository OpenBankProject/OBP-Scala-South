package com.tesobe.obp

import java.util.UUID

import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._
/**
  * Created by slavisa on 12/27/16.
  */
trait KafkaConfig {

  val config = ConfigFactory.load()

  def bootstrapServers = {
    Option(config.getString("bootstrap.servers")).getOrElse {
      throw new IllegalArgumentException("Environment variable bootstrap.servers is missing. Start the application with -Dbootstrap.servers=\"localhost:9092\"")
    }
  }

  val clientId = UUID.randomUUID().toString
  val groupId = "obp-socgen"//UUID.randomUUID().toString

  val autoOffsetResetConfig = "earliest"
  val maxWakeups = 50
  val completionTimeout =  FiniteDuration(config.getInt("business.timeout")*1000 - 450, MILLISECONDS)

}