package com.tesobe.obp

import java.util.UUID

import com.typesafe.config.ConfigFactory
/**
  * Created by slavisa on 12/27/16.
  */
trait KafkaConfig {

  def bootstrapServers = {
    val config = ConfigFactory.load()
    Option(config.getString("bootstrap.servers")).getOrElse {
      throw new IllegalArgumentException("Environment variable bootstrap.servers is missing. Start the application with -Dbootstrap.servers=\"localhost:9092\"")
    }
  }

  def clientId = UUID.randomUUID().toString
  def autoOffsetResetConfig = "earliest"

}