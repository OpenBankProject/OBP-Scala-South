package com.tesobe.obp.nov2016

import com.tesobe.obp.Request
import io.circe.generic.auto._
import io.circe.parser.decode
import io.circe.syntax._

/**
  * Worker trait for gathering data from local file and serializing it
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
trait Decoder {

  val BankNotFound = "OBP-30001: Bank not found. Please specify a valid value for BANK_ID."

  def response(request: Request): String = {
    val json = scala.io.Source.fromResource("example_import_Nov2016.json").getLines().mkString
    decode[com.tesobe.obp.nov2016.Example](json) match {
      case Left(err) => err.getMessage
      case Right(example) => extractQuery(request) match {
        case ("bank", "get") =>
          example.banks.filter(_.id == Some(request.bankId)).headOption match {
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

  /**
    * All subsequent case classes must be the same structure as it is defined on North Side.
    *
    */
  case class BankN(bankId: Option[String],
                   name: Option[String],
                   logo: Option[String],
                   url: Option[String]
                  )

}

object Decoder extends Decoder