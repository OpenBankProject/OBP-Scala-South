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
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}

import scala.concurrent.Future

/**
  * Created by slavisa on 6/4/17.
  */
class SouthKafkaStreamsActor(implicit val materializer: ActorMaterializer) extends Actor with KafkaConfig with StrictLogging {

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
    Future(
      ProducerMessage.Message(
        new ProducerRecord(topic, 0, key, value),
        offset)
    )
  }

  private val eventualAuditedMessage: ((String, CommittableMessage[String, String], String) => Future[Message[String, String, CommittableOffset]]) = { (topic, msg, value) =>
    //auditProducer.send(new ProducerRecord[Array[Byte], Array[Byte]](topic, msg.record.value(), value))
    eventualMessage(topic, msg.record.key(), value, msg.committableOffset)
  }

  private val process: ((Topic, Business) => Source[Message[String, String, CommittableOffset], Consumer.Control]) = { (topic, logic) =>
    Consumer
      .committableSource(consumerSettings, Subscriptions.topics(topic.request))
      .mapAsync(3) { x =>
        val f = logic(x)
        f.recover {
          case e: Throwable => {
            logger.error(e.getMessage)
            (x, "{\"count\": \"\", \"data\": [], \"state\": \"\", \"pager\": \"\", \"target\": \"banks\"}")
          }
        }
      }
      .mapAsync(3) { pr =>
        logger.info(topic.response + ": " + pr._2)
        eventualAuditedMessage(topic.response, pr._1, pr._2)
      }
  }

  override def receive: Receive = {
    case tp: TopicBusiness =>
      initStream(tp.topic, tp.business)
    case _ =>
      logger.error("Unexpected message")
      throw new Exception("Unexpected message")
  }

  private def initStream(tp: (Topic, Business)) = {
    process(tp._1, tp._2).runWith(Producer.commitableSink(producerSettings))
  }

}


case class Request(name: Option[String],
                   username: Option[String],
                   password: Option[String],
                   version: String,
                   north: Option[String],
                   target: Option[String],
                   userId: Option[String],
                   bankId: Option[String],
                   source: Option[String])
case class Response(count: Option[Long] = None,
                    data: Seq[BankN],
                    state: Option[String] = None,
                    pager: Option[String] = None,
                    target: Option[String] = None
                   )

object SouthKafkaStreamsActor {
  type Business = CommittableMessage[String, String] => Future[(CommittableMessage[String, String], String)]

  case class Topic(request: String, response: String)

  case class TopicBusiness(topic: Topic, business: Business)

  final val name = "SouthKafkaStreamsActor"

}