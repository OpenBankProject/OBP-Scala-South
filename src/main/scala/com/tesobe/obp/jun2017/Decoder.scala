package com.tesobe.obp.june2017

import java.util.Date

import com.tesobe.obp.{Config, Util}
import io.circe.Error
import io.circe.generic.auto._
import io.circe.parser.decode


/**
  * Responsible for processing requests based on local example_import_june2017.json file.
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
trait Decoder extends MappedDecoder with Config{

  def getBanks(getBanks: GetBanks) = {
    decodeLocalFile match {
      case Left(_) => Banks(getBanks.authInfo, List.empty[InboundBank])
      case Right(x) => Banks(getBanks.authInfo, x.banks.map(mapBankToInboundBank))
    }
  }

  def getBank(getBank: GetBank) = {
    decodeLocalFile match {
      case Left(_) => BankWrapper(getBank.authInfo, None)
      case Right(x) =>
        x.banks.filter(_.id == Some(getBank.bankId)).headOption match {
          case Some(x) => BankWrapper(getBank.authInfo, Some(mapBankToInboundBank(x)))
          case None => BankWrapper(getBank.authInfo, None)
        }
    }
  }

  def getUser(getUserbyUsernamePassword: GetUserByUsernamePassword) = {
    decodeLocalFile match {
      case Left(_) => UserWrapper(None)
      case Right(x) =>
        val userName = Some(getUserbyUsernamePassword.authInfo.username)
        val userPassword = Some(getUserbyUsernamePassword.password)
        x.users.filter(user => user.displayName == userName && user.password == userPassword).headOption match {
          case Some(x) => UserWrapper(Some(mapUserToInboundValidatedUser(x)))
          case None => UserWrapper(None)
        }
    }
  }
  
  def getAccounts(updateUserAccountViews: UpdateUserAccountViews) = {
    decodeLocalFile match {
      case Left(_) => OutboundUserAccountViewsBaseWapper(List.empty[InboundAccountJune2017])
      case Right(x) =>
        val userName = updateUserAccountViews.authInfo.username
        x.accounts.filter(account => account.owners.head == userName).headOption match {
          case Some(x) => OutboundUserAccountViewsBaseWapper(List(mapAdapterAccountToInboundAccountJune2017(x)))
          case None => OutboundUserAccountViewsBaseWapper(List.empty[InboundAccountJune2017])
        }
    }
  }
  
  def getAdapter(getAdapterInfo: GetAdapterInfo) = {
    AdapterInfo(data = Some(InboundAdapterInfo("", "OBP-Scala-South", "June2017", Util.gitCommit, (new Date()).toString)))
  }

  /*
   * Decodes example_import_june2017.json file to com.tesobe.obp.june2017.Example
   */
  private val decodeLocalFile: Either[Error, Example] = {
    val resource = scala.io.Source.fromResource("example_import_june2017.json")
    val lines = resource.getLines()
    val json = lines.mkString
    decode[com.tesobe.obp.june2017.Example](json)
  }

}


object Decoder extends Decoder