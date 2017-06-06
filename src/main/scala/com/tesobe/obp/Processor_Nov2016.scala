package com.tesobe.obp

import com.tesobe.obp.SouthKafkaStreamsActor.Business
import com.typesafe.scalalogging.StrictLogging
import spray.json.JsonParser

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by slavisa on 6/6/17.
  */
class Processor_Nov2016(extractor: RequestExtractor)(implicit val executionContext: ExecutionContext) extends KafkaConfig with StrictLogging with JSonSupport {

  import spray.json._

  val processor: Business = { msg =>
    logger.info(s"Processing ${msg.record.value}")
    val v = msg.record.value()
    Future {
      val request = JsonParser(v).convertTo[Request]
      val source = targetSource.replace("*", if (request.version == "") "" else "_" + request.version)
      val json = scala.io.Source.fromResource(source).getLines().mkString
      val example = JsonParser(json).convertTo[Example]
      val response =
        extractor.extractQuery(request) match {
          case ("bank", "get") =>
            val bankId = if (request.bankId == Some("1")) Some("obp-bank-x-gh") else Some("obp-bank-y-gh")
            Response(data = example.banks.filter(_.id == bankId).headOption.map(x => BankN(x.id, x.fullName, x.logo, x.website)).toSeq).toJson.compactPrint
          case ("banks", "get") =>
            Response(data = example.banks.map(x => BankN(x.id, x.fullName, x.logo, x.website))).toJson.compactPrint
          case _ =>
            Response(data = example.banks.map(x => BankN(x.id, x.fullName, x.logo, x.website))).toJson.compactPrint
        }
      (msg, response)
    }
  }

}

object Processor_Nov2016 {
  def apply(requestExtractor: RequestExtractor)(implicit executionContext: ExecutionContext) = new Processor_Nov2016(requestExtractor)
}