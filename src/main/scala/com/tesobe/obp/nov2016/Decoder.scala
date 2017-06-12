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

  val BankNotFound = "OBP-30001: Bank not found. Please specify a valid value for BANK_ID."

  def response(request: Request): String = {
    val json = scala.io.Source.fromResource("example_import_Nov2016.json").getLines().mkString
    decode[com.tesobe.obp.nov2016.Example](json) match {
      case Left(err) => err.getMessage
      case Right(example) => extractQuery(request) match {
        case ("bank", "get") =>
          val bankId = if (request.bankId == Some("1")) Some("obp-bank-x-gh") else if (request.bankId == Some("2")) Some("obp-bank-y-gh") else None
          example.banks.filter(_.id == bankId).headOption match {
            case Some(x) => Map("data" -> BankN(x.id, x.fullName, x.logo, x.website)).asJson.noSpaces
            case None => Map("data" -> BankNotFound).asJson.noSpaces
          }
        case ("banks", "get") =>
          val data = example.banks.map(x => BankN(x.id, x.fullName, x.logo, x.website))
          Map("data" -> data).asJson.noSpaces
        case _ =>
          Map("data" -> "Error, unrecognised request").asJson.noSpaces
      }
    }
  }


  private def extractQuery(request: Request): (String, String) = {
    (request.target.get, request.name.get)
  }

  case class BankN(bankId: Option[String],
                   name: Option[String],
                   logo: Option[String],
                   url: Option[String]
                  )

}

object Decoder extends Decoder