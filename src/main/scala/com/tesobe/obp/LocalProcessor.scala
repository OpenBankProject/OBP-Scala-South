package com.tesobe.obp

import akka.kafka.ConsumerMessage.CommittableMessage
import akka.stream.Materializer
import com.tesobe.obp.SouthKafkaStreamsActor.Business
import com.tesobe.obp.jun2017._
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.{ExecutionContext, Future}
import io.circe.parser.decode
import io.circe.generic.auto._
import io.circe.syntax._

/**
  * Responsible for processing requests from North Side using local json files as data sources.
  *
  */
class LocalProcessor(implicit executionContext: ExecutionContext, materializer: Materializer) extends StrictLogging with Config {

  /**
    * Processes message that comes from generic 'Request'/'Response' topics.
    * It has to resolve version from request first and based on that employ corresponding Decoder to extract response.
    * For convenience it is done in private method.
    *
    * @return Future of tuple2 containing message given from client and response given from corresponding Decoder.
    *         The form is defined in SouthKafkaStreamsActor
    */
  def generic: Business = { msg =>
    logger.info(s"Processing ${msg.record.value}")
    Future(msg, getResponse(msg))
  }

  /**
    * Processes message that comes from 'GetBanks' topic
    *
    * @return
    */
  def banksFn: Business = { msg =>
    /* call Decoder for extracting data from source file */
    logger.info(s"Processing banksFn ${msg.record.value}")
    val response: (GetBanks => Banks) = { q => com.tesobe.obp.jun2017.Decoder.getBanks(q) }
    val r = decode[GetBanks](msg.record.value()) match {
      case Left(e) => ""
      case Right(x) => response(x).asJson.noSpaces
    }
    Future(msg, r)
  }

  def bankFn: Business = { msg =>
    logger.info(s"Processing bankFn ${msg.record.value}")
    /* call Decoder for extracting data from source file */
    val response: (GetBank => BankWrapper) = { q => com.tesobe.obp.jun2017.Decoder.getBank(q) }
    val r = decode[GetBank](msg.record.value()) match {
      case Left(e) => ""
      case Right(x) => response(x).asJson.noSpaces
    }
    Future(msg, r)
  }
  
  def accountsFn: Business = { msg =>
    val response: (GetAccounts => AccountsWrapper) ={ q => com.tesobe.obp.jun2017.Decoder.getAccounts(q)}
    
  }
  def adapterFn: Business = { msg =>
    logger.info(s"Processing adapterFn ${msg.record.value}")
    /* call Decoder for extracting data from source file */
    val response: (GetAdapterInfo => AdapterInfo) = { q => com.tesobe.obp.jun2017.Decoder.getAdapter(q) }
    val r = decode[GetAdapterInfo](msg.record.value()) match {
      case Left(e) => ""
      case Right(x) => response(x).asJson.noSpaces
    }
    Future(msg, r)
  }


  private def getResponse(msg: CommittableMessage[String, String]): String = {
    decode[Request](msg.record.value()) match {
      case Left(e) => e.getLocalizedMessage
      case Right(r) =>
        val rr = r.version.isEmpty match {
          case true => r.copy(version = r.messageFormat)
          case false => r.copy(messageFormat = r.version)
        }
        rr.version match {
          case Some("Nov2016") => com.tesobe.obp.nov2016.Decoder.response(rr)
          case Some("Mar2017") => com.tesobe.obp.mar2017.Decoder.response(rr)
          case Some("Jun2017") => com.tesobe.obp.jun2017.Decoder.response(rr)
          case _ => com.tesobe.obp.nov2016.Decoder.response(rr)
        }
    }
  }
}

object LocalProcessor {
  def apply()(implicit executionContext: ExecutionContext, materializer: Materializer): LocalProcessor =
    new LocalProcessor()
}

case class FileProcessingException(message: String) extends RuntimeException(message)
