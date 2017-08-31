package com.tesobe.obp

import java.util.UUID

import com.tesobe.obp.SouthKafkaStreamsActor.TopicPair
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

/**
  * Main configuration utility
  *
  * Open Bank Project - Leumi Adapter
  * Copyright (C) 2016-2017, TESOBE Ltd.This program is free software: you can redistribute it and/or modify
  * it under the terms of the GNU Affero General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU Affero General Public License for more details.You should have received a copy of the GNU Affero General Public License
  * along with this program.  If not, see <http://www.gnu.org/licenses/>.Email: contact@tesobe.com
  * TESOBE Ltd
  * Osloerstrasse 16/17
  * Berlin 13359, GermanyThis product includes software developed at TESOBE (http://www.tesobe.com/)
  * This software may also be distributed under a commercial license from TESOBE Ltd subject to separate terms.
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
    *  obp.June2017.N.GetBank
    *  obp.June2017.S.GetBank
    */
    TopicPair(
      s"obp.${version}.N." + className.replace("$", ""),
      s"obp.${version}.S." + className.replace("$", "")
    )
}