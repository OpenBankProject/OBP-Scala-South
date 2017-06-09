package com.tesobe.obp.nov2016

import com.tesobe.obp.Request
import io.circe.Error
import io.circe.generic.auto._
import io.circe.parser.decode
import io.circe.syntax._

/**
  * Created by slavisa on 6/8/17.
  */
trait Decoder {

  def response(request: Request): String = {
    val json = scala.io.Source.fromResource("example_import_Nov2016.json").getLines().mkString
    val r = decode[com.tesobe.obp.nov2016.Example](json) match {
      case Left(err) => err.getMessage
      case Right(example) => extractQuery(request) match {
        case ("bank", "get") =>
          val bankId = if (request.bankId == Some("1")) Some("obp-bank-x-gh") else if (request.bankId == Some("2")) Some("obp-bank-y-gh") else None
          (Response(data = example.banks.filter(_.id == bankId).headOption.map(x => BankN(x.id, x.fullName, x.logo, x.website)).toSeq))
        case ("banks", "get") =>
          val r = Response(data = example.banks.map(x => BankN(x.id, x.fullName, x.logo, x.website)))
          r
        case _ =>
          Response(data = example.banks.map(x => BankN(x.id, x.fullName, x.logo, x.website)))
      }
    }
    r.asJson.noSpaces
  }


  private def extractQuery(request: Request): (String, String) = {
    (request.target.get, request.name.get)
  }

  case class BankN(bankId: Option[String],
                   name: Option[String],
                   logo: Option[String],
                   url: Option[String]
                  )

  case class Response(count: Option[Long] = None,
                      data: Seq[BankN],
                      state: Option[String] = None,
                      pager: Option[String] = None,
                      target: Option[String] = None
                     )

}

object Decoder extends Decoder