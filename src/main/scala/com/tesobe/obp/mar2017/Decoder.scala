package com.tesobe.obp.mar2017

import com.tesobe.obp.{Request}
import io.circe.parser.decode
import io.circe.generic.auto._
import io.circe.syntax._


/**
  * Responsible for processing requests based on local example_import_mar2017.json file.
  *
  */
trait Decoder {

  val BankNotFound = "OBP-30001: Bank not found. Please specify a valid value for BANK_ID."

  def response(request: Request): String = {
    val resource = scala.io.Source.fromResource("example_import_mar2017.json")
    val lines = resource.getLines()
    val json = lines.mkString
    val d = decode[com.tesobe.obp.mar2017.Example](json)
    d match {
      case Left(err) => Map("data" -> err.getMessage).asJson.noSpaces
      case Right(example) =>
        extractQuery(request) match {
          case Some("obp.get.Bank") =>
            example.banks.filter(_.id == Some(request.bankId)).headOption match {
              case Some(x) => Map("data" -> mapBankN(x)).asJson.noSpaces
              case None => Map("data" -> BankN(Some(BankNotFound), None, None, None, None)).asJson.noSpaces
            }
          case Some("obp.get.Banks") =>
            val data = example.banks.map(x => mapBankN(x))
            Map("data" -> data).asJson.noSpaces

          case Some("obp.get.User") =>
            example.users.filter(_.displayName == request.username).filter(_.password == request.password).headOption match {
              case Some(x) => Map("data" -> mapUserN(x)).asJson.noSpaces
              case None => Map("data" -> UserN(Some(BankNotFound), None, None)).asJson.noSpaces
            }
          case _ =>
            Map("data" -> "Error, unrecognised request").asJson.noSpaces
        }
    }
  }

  private def mapBankN(x: Bank) = {
    BankN(None, x.id, x.fullName, x.logo, x.website)
  }

  private def mapUserN(x: User) = {
    UserN(None, x.email, x.displayName)
  }

  private def extractQuery(request: Request): Option[String] = {
    request.action
  }

  /**
    * All subsequent case classes must be the same structure as it is defined on North Side.
    *
    */
  case class UserN(
                    errorCode: Option[String],
                    email: Option[String],
                    displayName: Option[String]
                  )

  case class BankN(
                    errorCode: Option[String],
                    bankId: Option[String],
                    name: Option[String],
                    logo: Option[String],
                    url: Option[String]
                  )

}


object Decoder extends Decoder
