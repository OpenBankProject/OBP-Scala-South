package com.tesobe.obp

import akka.stream.Materializer
import com.tesobe.obp.SouthKafkaStreamsActor.Business
import com.typesafe.scalalogging.StrictLogging
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by slavisa on 6/6/17.
  */
class CirceProcessor(extractor: RequestExtractor)(implicit executionContext: ExecutionContext, materializer: Materializer) extends StrictLogging with KafkaConfig {

  def processor: Business = { msg =>
    logger.info(s"Processing ${msg.record.value}")
    val re = for {
      request<- decode[Request](msg.record.value()).right
      banks <- {
        val source = targetSource.replace("*", if (request.version == "") "" else "_" + request.version)
        val json = scala.io.Source.fromResource(source).getLines().mkString
        decode[Example](json).map(_.banks).right
      }
      r <- Right(extractor.extractQuery(request) match {
        case ("bank", "get") =>
          val bankId = if (request.bankId == Some("1")) Some("obp-bank-x-gh") else Some("obp-bank-y-gh")
          (Response(data = banks.filter(_.id == bankId).headOption.map(x => BankN(x.id, x.fullName, x.logo, x.website)).toSeq))
        case ("banks", "get") =>
          Response(data = banks.map(x => BankN(x.id, x.fullName, x.logo, x.website)))
        case _ =>
          Response(data = banks.map(x => BankN(x.id, x.fullName, x.logo, x.website)))
      }).right
    } yield {
      r
    }
    Future(msg, re.getOrElse(Response(data = Seq.empty[BankN])).asJson.noSpaces)
  }

}

object CirceProcessor {
  def apply(extractor: RequestExtractor)(implicit executionContext: ExecutionContext, materializer: Materializer): CirceProcessor =
    new CirceProcessor(extractor)
}

case class FileProcessingException(message: String) extends RuntimeException(message)
