package com.tesobe.obp

import akka.actor.Actor
import akka.kafka.ConsumerMessage.{CommittableMessage, CommittableOffset}
import akka.kafka.ProducerMessage.Message
import akka.kafka._
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.stream.ActorMaterializer
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

  override def receive: Receive = {
    case Init(topic) =>
      initStream(topic)
  }

  private def initStream(topic: Topic) = {
    val consumerSettings: ConsumerSettings[String, String] = {
      ConsumerSettings(context.system, new StringDeserializer, new StringDeserializer)
        .withBootstrapServers(bootstrapServers)
        .withGroupId(groupId)
        .withClientId(clientId)
        .withMaxWakeups(50)
        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetResetConfig)
    }

    val producerSettings = ProducerSettings(context.system, new StringSerializer, new StringSerializer)
      .withBootstrapServers(bootstrapServers)
      .withProperty("batch.size", "0")

    val eventualMessage: ((String, String, String, CommittableOffset) => Future[Message[String, String, CommittableOffset]]) = { (topic, key, value, offset) =>
      Future(
        ProducerMessage.Message(
          new ProducerRecord(topic, 0, key, value),
          offset)
      )
    }

    val eventualAuditedMessage: ((String, CommittableMessage[String, String], String) => Future[Message[String, String, CommittableOffset]]) = { (topic, msg, value) =>
      //auditProducer.send(new ProducerRecord[Array[Byte], Array[Byte]](topic, msg.record.value(), value))
      eventualMessage(topic, msg.record.key(), value, msg.committableOffset)
    }

    Consumer
      .committableSource(consumerSettings, Subscriptions.topics(topic.request))
      .mapAsync(3) { x =>
        val f = business(x)
        f.recover {
          case e: Throwable => {
            logger.error(e.getMessage)
            (x, "")
          }
        }
      }
      .mapAsync(3) { pr =>
        logger.info(topic.response + ": " + pr._2)
        eventualAuditedMessage(topic.response, pr._1, pr._2)
      }
      .runWith(Producer.commitableSink(producerSettings))
  }

  private val business: Business = {
    x =>
      Future {
        val v = x.record.value()

        val response = if (v.contains("\"bankId\":\"index\"") || v.contains("\"north\":\"getBanks\"")) {
          BANKS_RESP
        } else if (v.contains("\"bankId\":\"1\"")) {
          BANK1_RESP;
        } else if (v.contains("\"bankId\":\"2\"")) {
          BANK2_RESP;
        } else {
          "{\"count\": \"\", \"data\": [], \"state\": \"\", \"pager\": \"\", \"target\": \"banks\"}";
        }
        (x, response)
      }
  }
}

object SouthKafkaStreamsActor {
  type Business = CommittableMessage[String, String] => Future[(CommittableMessage[String, String], String)]

  case class Topic(request: String, response: String)

  case class Init(topic: Topic)

  final val name = "SouthKafkaStreamsActor"

  //just for debug in devel phase

  val BANKS_RESP = "{\"count\": \"\", \"data\": [{\"url\": \"https://www.example.com\", \"logo\": \"https://static.openbankproject.com/images/sandbox/bank_x.png\", \"name\": \"The Bank of X\", \"bankId\": \"obp-bank-x-gh\"}, {\"url\": \"https://www.example.com\", \"logo\": \"https://static.openbankproject.com/images/sandbox/bank_y.png\", \"name\": \"The Bank of Y\", \"bankId\": \"obp-bank-y-gh\"}], \"state\": \"\", \"pager\": \"\", \"target\": \"banks\"}"
  val BANK1_RESP = "{" + "  \"data\": {" + "    \"url\": \"https://www.example.com\"," + "    \"logo\": \"https: //static.openbankproject.com/images/sandbox/bank_x.png\"," + "    \"name\": \"The Bank of X\"," + "    \"bankId\": \"obp-bank-x-gh\"" + "  }," + "}"
  val BANK2_RESP = "{" + "  \"data\": {" + "    \"url\": \"https://www.example.com\"," + "    \"logo\": \"https: //static.openbankproject.com/images/sandbox/bank_y.png\"," + "    \"name\": \"The Bank of Y\"," + "    \"bankId\": \"obp-bank-y-gh\"" + "  }," + "}"
}