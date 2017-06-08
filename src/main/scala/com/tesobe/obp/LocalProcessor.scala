package com.tesobe.obp

import akka.kafka.ConsumerMessage.CommittableMessage
import akka.stream.Materializer
import com.tesobe.obp.SouthKafkaStreamsActor.Business
import com.typesafe.scalalogging.StrictLogging
import io.circe.Error
import io.circe.generic.auto._
import io.circe.parser._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by slavisa on 6/6/17.
  */
class LocalProcessor(extractor: RequestExtractor)(implicit executionContext: ExecutionContext, materializer: Materializer) extends StrictLogging with Config {

  def processor: Business = { msg =>
    logger.info(s"Processing ${msg.record.value}")
    Future(msg, getResponse(msg))
  }

  private def getResponse(msg: CommittableMessage[String, String]): String = {
    decode[Request](msg.record.value()) match {
      case Left(e) => e.getLocalizedMessage
      case Right(r) =>
        val rr = r.version.isEmpty match {
          case true => r.copy(version = r.messageFormat)
          case false => r.copy(messageFormat = r.version)
        }
        val t = rr.version match {
          case Some("Nov2016") => com.tesobe.obp.nov2016.Decoder.response(rr)
          case Some("Mar2017") => com.tesobe.obp.mar2017.Decoder.response(rr)
          case _ => com.tesobe.obp.nov2016.Decoder.response(rr)
        }
        t.getOrElse("")
    }
  }
}

object LocalProcessor {
  def apply(extractor: RequestExtractor)(implicit executionContext: ExecutionContext, materializer: Materializer): LocalProcessor =
    new LocalProcessor(extractor)
}

case class FileProcessingException(message: String) extends RuntimeException(message)
