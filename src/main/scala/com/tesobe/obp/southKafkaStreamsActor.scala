package com.tesobe.obp

import akka.actor.Actor
import akka.kafka.ConsumerMessage.{CommittableMessage, CommittableOffset}
import akka.kafka.ProducerMessage.Message
import akka.kafka._
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import com.typesafe.scalalogging.StrictLogging
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}

import scala.concurrent.Future

/**
  * Responsible for talking to kafka.
  * Receives messages from kafka and sends result of applied business logic on the same partition but on 'Response' topic.
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
class SouthKafkaStreamsActor(implicit val materializer: ActorMaterializer) extends Actor with Config with StrictLogging {

  import SouthKafkaStreamsActor._
  import context.dispatcher

  private val consumerSettings: ConsumerSettings[String, String] = {
    ConsumerSettings(context.system, new StringDeserializer, new StringDeserializer)
      .withBootstrapServers(bootstrapServers)
      .withGroupId(groupId)
      .withClientId(clientId)
      .withMaxWakeups(50)
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetResetConfig)
  }

  private val producerSettings = ProducerSettings(context.system, new StringSerializer, new StringSerializer)
    .withBootstrapServers(bootstrapServers)
    .withProperty("batch.size", "0")

  // Here will send the message to kafka broker:
  // (topic, key, value, offset) --> ProducerMessage.Message(new ProducerRecord(topic, partition, key, value),offset)
  private val eventualMessage: ((String, String, String, CommittableOffset) => Future[Message[String, String, CommittableOffset]]) = { (topic, key, value, offset) =>
    // key is from North Side and With the same key, partition and share the same TopicPair to send the message back . 
    val partition = key.split("_")(0).toInt 
    logger.debug(s"partition: $partition")
    Future(
      ProducerMessage.Message(
        new ProducerRecord(topic, partition, key, value),
        offset)
    )
  }
  // extract (topic, msg, value) --> (topic, msg.record.key(), value, msg.committableOffset)
  // Do not use the auditProducer for now ?? only call the eventualMessage ? 
  private val eventualAuditedMessage: ((String, CommittableMessage[String, String], String) => Future[Message[String, String, CommittableOffset]]) = { (topic, msg, value) =>
    //auditProducer.send(new ProducerRecord[String, String](topic, msg.record.value(), value))
    eventualMessage(topic, msg.record.key(), value, msg.committableOffset)
  }

  //Communication with Kafka, 
  // 1 Consumer read the data from Kafka
  // 2 call the business logic to process the input message
  // 3 send back the message to kafka.
  // Note: Here South-side shares the same key, partition and TopicParis with North-side. 
  //       So the North-side know exactly how to communicate with it.
  private val process: ((TopicPair, Business) => Source[Message[String, String, CommittableOffset], Consumer.Control]) = { (topicPair, business) =>
    val topicRequest = topicPair.request
    val topicResponse = topicPair.response
    val topicAndItsPartitions = buildAllPartitionsForOneTopic(topicRequest)
    Consumer
      .committableSource(
        consumerSettings, 
        Subscriptions.assignment(topicAndItsPartitions)//The consumer need subscribe all the partitions for one topic
      )
      .mapAsync(3) { consumerMessage =>
        logger.debug(s"Kafka-get-message : TopicRequest(${topicRequest}): ${consumerMessage.record.value()}")
        val future = business(consumerMessage)
        future.recover {
          case e: Throwable => {
            logger.error(e.getMessage)
            (consumerMessage, "")
          }
        }
      }
      .mapAsync(3) { consumerMessageBusiness =>
        if(consumerMessageBusiness._2 ==""){
          val errorMessage = s"Kafka-send-message :topicResponse(${topicResponse }): is empty, please compare your case class fields for both sides !"
          logger.error(errorMessage)
          //For Debug, when it is stable, just uncomment this Exception
          throw new RuntimeException(errorMessage)
        }
        else
          logger.debug(s"Kafka-send-message : ${topicResponse}: ${consumerMessageBusiness._2}")
        eventualAuditedMessage(topicResponse, consumerMessageBusiness._1, consumerMessageBusiness._2)
      }
  }
  
  
  
  /**
    * Create the partition list according to the input requestTopic and kafkaPartitions(from props application.conf)
    * @param requestTopic the requestTopic is create from North Side, in South side, only Consumer the topic
    * @return a set contains all the partitions from the input requestTopic
    */
  private def buildAllPartitionsForOneTopic(requestTopic: String): Set[TopicPartition] = {
    ((0 to (kafkaPartitions - 1)) map (new TopicPartition(requestTopic, _))).toSet
  }
  
  /**
    * Message contains topics and business logic that will be applied on message are defined in message.
    *
    */
  override def receive: Receive = {
    case tp: BusinessTopic =>
      initStream(tp.topicPair, tp.business)
    case tps: Seq[BusinessTopic] =>
      tps foreach (tp => initStream(tp.topicPair, tp.business))
    case _ =>
      logger.error("Unexpected message")
      throw new Exception("Unexpected message")
  }

  private def initStream(tp: (TopicPair, Business)) = {
    process(tp._1, tp._2).runWith(Producer.commitableSink(producerSettings))
  }

}

/**
  * Contains all tags used in various kafka connector versions on North Side.
  *
  * @param name
  * @param username
  * @param password
  * @param messageFormat
  * @param action
  * @param version
  * @param north
  * @param target
  * @param userId
  * @param bankId
  * @param source
  */
case class Request(name: Option[String],
                   username: Option[String],
                   password: Option[String],
                   messageFormat: Option[String],
                   action: Option[String],
                   version: Option[String],
                   north: Option[String],
                   target: Option[String],
                   userId: Option[String],
                   bankId: Option[String],
                   source: Option[String])

object SouthKafkaStreamsActor {
  
  /**
    * This is a function, input is KafkaConsumerMessage, output is Future(KafkaConsumerMessage, KafkaProducerMessage.value)
    * Eg: com.tesobe.obp.LocalProcessor#banksFn()
    *   banksFn() is a partition function: 
    *    1 Process the inputMessage and get the result.
    *    2 Return the inputMessage and result in Future
    */
  type Business = CommittableMessage[String, String] => Future[(CommittableMessage[String, String], String)]

  /**
    * This case class design a pair of Topic, for both North and South side.
    * They are a pair
    * @param request  eg: obp.June2017.N.GetBanks
    * @param response eg: obp.June2017.S.GetBanks
    */
  case class TopicPair(request: String, response: String)

  /**
    * In fact tuple to overcome compiler warnings regarding type erasure.
    *
    * @param topicPair defines topic (request) to which the consumer will be subscribed and topic (response) on which messages will be sent by producer
    * @param business defines business logic that will be applied. For example LocalProcessor.bankFn or LocalProcessor.banksFn
    */
  case class BusinessTopic(topicPair: TopicPair, business: Business)

  final val name = "SouthKafkaStreamsActor"

}