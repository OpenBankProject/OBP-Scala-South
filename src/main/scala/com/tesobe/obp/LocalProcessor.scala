package com.tesobe.obp

import akka.kafka.ConsumerMessage.CommittableMessage
import akka.stream.Materializer
import com.tesobe.obp.SouthKafkaStreamsActor.Business
import com.tesobe.obp.june2017._
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.{ExecutionContext, Future}
import io.circe.parser.decode
import io.circe.generic.auto._
import io.circe.syntax._

/**
  * Responsible for processing requests from North Side using local json files as data sources.
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
  
  def adapterFn: Business = { msg =>
    logger.debug(s"Processing adapterFn ${msg.record.value}")
    /* call Decoder for extracting data from source file */
    val response: (GetAdapterInfo => AdapterInfo) = { q => com.tesobe.obp.june2017.Decoder.getAdapter(q) }
    val r = decode[GetAdapterInfo](msg.record.value()) match {
      case Left(e) => throw new RuntimeException(" !!! --- This can not be empty, please compare the case class for both sides, only for debugging ");
      case Right(x) => response(x).asJson.noSpaces
    }
    Future(msg, r)
  }
  
  /**
    * Processes message that comes from 'GetBanks' topic
    *
    * @return
    */
  def banksFn: Business = { msg =>
    /* call Decoder for extracting data from source file */
    logger.debug(s"Processing banksFn ${msg.record.value}")
    val response: (GetBanks => Banks) = { q => com.tesobe.obp.june2017.Decoder.getBanks(q) }
    val r = decode[GetBanks](msg.record.value()) match {
      case Left(e) => throw new RuntimeException(" !!! --- This can not be empty, please compare the case class for both sides, only for debugging ");
      case Right(x) => response(x).asJson.noSpaces
    }
    Future(msg, r)
  }

  def bankFn: Business = { msg =>
    logger.debug(s"Processing bankFn ${msg.record.value}")
    /* call Decoder for extracting data from source file */
    val response: (GetBank => BankWrapper) = { q => com.tesobe.obp.june2017.Decoder.getBank(q) }
    val r = decode[GetBank](msg.record.value()) match {
      case Left(e) => throw new RuntimeException(" !!! --- This can not be empty, please compare the case class for both sides, only for debugging ");
      case Right(x) => response(x).asJson.noSpaces
    }
    Future(msg, r)
  }
  
  def userFn: Business = { msg =>
    logger.debug(s"Processing userFn ${msg.record.value}")
    /* call Decoder for extracting data from source file */
    val response: (GetUserByUsernamePassword => UserWrapper) = { q => com.tesobe.obp.june2017.Decoder.getUser(q) }
    val r = decode[GetUserByUsernamePassword](msg.record.value()) match {
      case Left(e) => throw new RuntimeException(" !!! --- This can not be empty, please compare the case class for both sides, only for debugging ");
      case Right(x) => response(x).asJson.noSpaces
    }
    Future(msg, r)
  }
  
  def accountsFn: Business = { msg =>
    logger.debug(s"Processing accountsFn ${msg.record.value}")
    /* call Decoder for extracting data from source file */
    val response: (UpdateUserAccountViews => OutboundUserAccountViewsBaseWapper) = { q => com.tesobe.obp.june2017.Decoder.getAccounts(q) }
    val r = decode[UpdateUserAccountViews](msg.record.value()) match {
      case Left(e) => throw new RuntimeException(" !!! --- This can not be empty, please compare the case class for both sides, only for debugging ");
      case Right(x) => response(x).asJson.noSpaces
    }
    Future(msg, r)
  }
  
  def bankAccountsFn: Business = {msg =>
    logger.debug(s"Processing bankAccountsFn ${msg.record.value}")
    //    /* call Decoder for extracting data from source file */
    val response: (OutboundGetAccounts => InboundBankAccounts) = { q => com.tesobe.obp.june2017.Decoder.getBankAccounts(q) }
    val r = decode[OutboundGetAccounts](msg.record.value()) match {
      case Left(e) => throw new RuntimeException(" !!! --- This can not be empty, please compare the case class for both sides, only for debugging ");
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
          case Some("June2017") => com.tesobe.obp.june2017.Decoder.response(rr)
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
