package com.tesobe.obp.june2017

import com.tesobe.obp.Request
import io.circe.generic.auto._
import io.circe.parser.decode
import io.circe.syntax._

/**
  * Support for old style messaging.
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
trait MappedDecoder {

  val BankNotFound = "OBP-30001: Bank not found. Please specify a valid value for BANK_ID."

  def response(request: Request): String = {
    val resource = scala.io.Source.fromResource("example_import_june2017.json")
    val lines = resource.getLines()
    val json = lines.mkString
    val d = decode[com.tesobe.obp.june2017.Example](json)
    d match {
      case Left(err) => Map("data" -> err.getMessage).asJson.noSpaces
      case Right(example) =>
        extractQuery(request) match {
          case Some("obp.get.Bank") =>
            example.banks.filter(_.id == Some(request.bankId)).headOption match {
              case Some(x) => Map("data" -> mapBankToInboundBank(x)).asJson.noSpaces
              case None => Map("data" -> InboundBank(BankNotFound, "", "", "", "")).asJson.noSpaces
            }
          case Some("obp.get.Banks") =>
            val data = example.banks.map(mapBankToInboundBank)
            Map("data" -> data).asJson.noSpaces

          case Some("obp.get.User") =>
            example.users.filter(_.displayName == request.username).filter(_.password == request.password).headOption match {
              case Some(x) => Map("data" -> mapUserToInboundValidatedUser(x)).asJson.noSpaces
              case None => Map("data" -> InboundValidatedUser(Some(BankNotFound), None, None)).asJson.noSpaces
            }
          case _ =>
            Map("data" -> "Error, unrecognised request").asJson.noSpaces
      }
    }
  }

  def mapBankToInboundBank(x: Bank) = {
    InboundBank("", x.id.getOrElse(""), x.fullName.getOrElse(""), x.logo.getOrElse(""), x.website.getOrElse(""))
  }

  def mapUserToInboundValidatedUser(x: User) = {
    InboundValidatedUser(None, x.email, x.displayName)
  }
  
  def mapAdapterAccountToInboundAccountJune2017(x: Account) = {
    InboundAccountJune2017(errorCode = "", "cbs", bankId = x.bank.get, branchId = x.branchId.get, accountId = x.branchId.get, accountNumber = x.branchId.get, accountType = x.branchId.get, balanceAmount = x.branchId.get, balanceCurrency = x.branchId.get, owners = x.owners, viewsToGenerate = x.owners, bankRoutingScheme = x.bank.get, bankRoutingAddress = x.bank.get, branchRoutingScheme = x.bank.get, branchRoutingAddress = x.bank.get, accountRoutingScheme = x.bank.get, accountRoutingAddress = x.bank.get)
  }

  private def extractQuery(request: Request): Option[String] = {
    request.action
  }

}
