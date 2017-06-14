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
  * Receives messages from kafka and sends result of applied business logic on the same partition but on 'R' topic.
  *
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

  private val eventualMessage: ((String, String, String, CommittableOffset) => Future[Message[String, String, CommittableOffset]]) = { (topic, key, value, offset) =>
    val partition = key.split("_")(0).toInt
    logger.info(s"partition: $partition")
    Future(
      ProducerMessage.Message(
        new ProducerRecord(topic, partition, key, value),
        offset)
    )
  }

  private val eventualAuditedMessage: ((String, CommittableMessage[String, String], String) => Future[Message[String, String, CommittableOffset]]) = { (topic, msg, value) =>
    //auditProducer.send(new ProducerRecord[String, String](topic, msg.record.value(), value))
    eventualMessage(topic, msg.record.key(), value, msg.committableOffset)
  }

  private val process: ((Topic, Business) => Source[Message[String, String, CommittableOffset], Consumer.Control]) = { (topic, logic) =>
    Consumer
      .committableSource(consumerSettings, Subscriptions.assignment(buildPartitions(topic)))
      .mapAsync(3) { x =>
        val f = logic(x)
        f.recover {
          case e: Throwable => {
            logger.error(e.getMessage)
            (x, "")
          }
        }
      }
      .mapAsync(3) { pr =>
        logger.info(s"Response - ${topic.response}: ${pr._2}")
        eventualAuditedMessage(topic.response, pr._1, pr._2)
      }
  }

  private def buildPartitions(topic: Topic) = {
    ((0 to (kafkaPartitions - 1)) map (new TopicPartition(topic.request, _))).toSet
  }

  /**
    * Message contains topics and business logic that will be applied on message are defined in message.
    *
    */
  override def receive: Receive = {
    case tp: BusinessTopic =>
      initStream(tp.topic, tp.business)
    case tps: Seq[BusinessTopic] =>
      tps foreach (tp => initStream(tp.topic, tp.business))
    case _ =>
      logger.error("Unexpected message")
      throw new Exception("Unexpected message")
  }

  private def initStream(tp: (Topic, Business)) = {
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
  type Business = CommittableMessage[String, String] => Future[(CommittableMessage[String, String], String)]

  /**
    *
    * @param request
    * @param response
    */
  case class Topic(request: String, response: String)

  /**
    * In fact tuple to overcome compiler warnings regarding type erasure.
    *
    * @param topic defines topic (request) to which the consumer will be subscribed and topic (response) on which messages will be sent by producer
    * @param business defines business logic that will be applied. For example LocalProcessor.generic or LocalProcessor.banksFn
    */
  case class BusinessTopic(topic: Topic, business: Business)

  final val name = "SouthKafkaStreamsActor"

}