package com.tesobe.obp

import akka.actor.Actor
import akka.kafka.ConsumerMessage.{CommittableMessage, CommittableOffset}
import akka.kafka.ProducerMessage.Message
import akka.kafka._
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import spray.json.JsonParser

import scala.concurrent.Future

/**
  * Created by slavisa on 6/4/17.
  */
class SouthKafkaStreamsActor(implicit val materializer: ActorMaterializer) extends Actor with KafkaConfig with JSonSupport with StrictLogging {

  import SouthKafkaStreamsActor._
  import context.dispatcher

  override def receive: Receive = {
    case topic: Topic =>
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

    val flow = Consumer
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

    flow.runWith(Producer.commitableSink(producerSettings))
  }

  import spray.json._

  private val business: Business = { x =>
    val v = x.record.value()
    Future {
      val request = JsonParser(v).convertTo[Request]

      val source = config.getString("target.source").replace("*", if(request.version == "" ) "" else "_" +request.version)
      val json = scala.io.Source.fromResource(source).getLines().mkString
      val example = JsonParser(json).convertTo[Example]

      logger.info("++++++++++++++++" + request)
      
      val response =
        extractQuery(request) match {
          case "getBank" => Response(
            count = None,
            state = None,
            pager = None,
            target = None,
            data = example.banks.filter(_.id == request.bankId).map(x => BankN(x.id, x.fullName, x.logo, x.website))).toJson.compactPrint
          case "getBanks" => Response(
            count = None,
            state = None,
            pager = None,
            target = None,
            data = example.banks.map(x => BankN(x.id, x.fullName, x.logo, x.website))).toJson.compactPrint
          case _ => Response(
            count = None,
            state = None,
            pager = None,
            target = None,
            data = example.banks.map(x => BankN(x.id, x.fullName, x.logo, x.website))).toJson.compactPrint
        }
      (x, response)
    }
  }

  private def extractQuery(request: Request): String = {
    if(request.version == "Nov2016"){
      request.target.get
    }else {
      ""
    }
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
case class Response(count: Option[Long],
                    data: Seq[BankN],
                    state: Option[String],
                    pager: Option[String],
                    target: Option[String]
                   )
object SouthKafkaStreamsActor {
  type Business = CommittableMessage[String, String] => Future[(CommittableMessage[String, String], String)]

  case class Topic(request: String, response: String)

  final val name = "SouthKafkaStreamsActor"

  //just for debug in devel phase

  val BANKS_RESP = "{\"count\": \"\", \"data\": [{\"url\": \"https://www.example.com\", \"logo\": \"https://static.openbankproject.com/images/sandbox/bank_x.png\", \"name\": \"The Bank of X\", \"bankId\": \"obp-bank-x-gh\"}, {\"url\": \"https://www.example.com\", \"logo\": \"https://static.openbankproject.com/images/sandbox/bank_y.png\", \"name\": \"The Bank of Y\", \"bankId\": \"obp-bank-y-gh\"}], \"state\": \"\", \"pager\": \"\", \"target\": \"banks\"}"
  val A =                           "{\"data\": [{\"website\":\"https://www.example.com\",\"fullName\":\"The Bank of X\",\"logo\":\"https://static.openbankproject.com/images/sandbox/bank_x.png\",\"id\":\"obp-bank-x-gh\",\"shortName\":\"Bank X\"},{\"website\":\"https://www.example.com\",\"fullName\":\"The Bank of Y\",\"logo\":\"https://static.openbankproject.com/images/sandbox/bank_y.png\",\"id\":\"obp-bank-y-gh\",\"shortName\":\"Bank Y\"}]}"
  val BANK1_RESP = "{" + "  \"data\": {" + "    \"url\": \"https://www.example.com\"," + "    \"logo\": \"https: //static.openbankproject.com/images/sandbox/bank_x.png\"," + "    \"name\": \"The Bank of X\"," + "    \"bankId\": \"obp-bank-x-gh\"" + "  }," + "}"
  val BANK2_RESP = "{" + "  \"data\": {" + "    \"url\": \"https://www.example.com\"," + "    \"logo\": \"https: //static.openbankproject.com/images/sandbox/bank_y.png\"," + "    \"name\": \"The Bank of Y\"," + "    \"bankId\": \"obp-bank-y-gh\"" + "  }," + "}"

}