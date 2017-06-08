package com.tesobe.obp.mar2017

import com.tesobe.obp.{Request}
import io.circe.parser.decode
import io.circe.Error
import io.circe.generic.auto._
import io.circe.syntax._


/**
  * Created by slavisa on 6/8/17.
  */
trait Decoder {

  def response(request: Request): Either[Error, String] = {
    for {
      example <- {
        val resource = scala.io.Source.fromResource("example_import_mar2017.json")
        val lines = resource.getLines()
        val json = lines.mkString
        val d = decode[com.tesobe.obp.mar2017.Example](json).right
        d
      }
      r <- Right(extractQuery(request) match {
        case Some("obp.get.Bank") =>
          val bankId = if (request.bankId == Some("1")) Some("obp-bank-x-gh") else if (request.bankId == Some("2")) Some("obp-bank-y-gh") else None
          (Response(data = example.banks.filter(_.id == bankId).headOption.map(x => BankN(BankId(x.id), x.shortName, x.fullName, x.logo, x.website)).toSeq))
        case Some("obp.get.Banks") =>
          Response(data = example.banks.map(x => BankN(BankId(x.id), x.shortName, x.fullName, x.logo, x.website)))
        case _ =>
          Response(data = example.banks.map(x => BankN(BankId(x.id), x.shortName, x.fullName, x.logo, x.website)))
      }).right
    } yield {
      r.asJson.noSpaces
    }
  }


  private def extractQuery(request: Request): Option[String] = {
    request.action
  }

  case class BankId(val value: String)

  case class BankN(
                    bankId: BankId,
                    shortName: String,
                    fullName: String,
                    logoUrl: String,
                    websiteUrl: String
                  )

  case class Response(count: Option[Long] = None,
                      data: Seq[BankN],
                      state: Option[String] = None,
                      pager: Option[String] = None,
                      target: Option[String] = None
                     )

}


object Decoder extends Decoder
