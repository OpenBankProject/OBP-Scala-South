package com.tesobe.obp.nov2016

/**
  * Model based on example_import_nov2017.json
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
case class DriveUp(hours: String)

case class Licence(id: Option[String],
                   name: Option[String]
                  )

case class Meta(licence: Option[Licence])

case class Location(latitude: Double, longitude: Double)

case class Address(city: Option[String],
                   countryCode: Option[String],
                   line3: Option[String],
                   line2: Option[String],
                   line1: Option[String],
                   country: Option[String],
                   state: Option[String],
                   postCode: Option[String]
                  )

case class Lobby(hours: Option[String])

case class Branch(name: Option[String],
                  bankId: Option[String],
                  id: Option[String],
                  driveUp: Option[DriveUp],
                  meta: Option[Meta],
                  location: Option[Location],
                  address: Option[Address],
                  lobby: Option[Lobby]
                 )

case class Role(validationDate: Option[String],
                name: Option[String]
               )

case class User(password: Option[String],
                displayName: Option[String],
                email: Option[String],
                roles: Option[Seq[Role]]
               )

case class ThisAccount(id: Option[String],
                       bank: Option[String]
                      )

case class CounterPartySimple(name: Option[String],
                              id: Option[String]
                             )

case class Details(description: Option[String],
                   completed: Option[String],
                   newBalance: Option[String],
                   value: Option[String],
                   `type`: Option[String],
                   posted: Option[String]
                  )

case class Transaction(id: Option[String],
                       thisAccount: Option[ThisAccount],
                       counterparty: Option[CounterPartySimple],
                       userId: Option[String],
                       transactionChargePolicy: Option[String],
                       toCounterpartyBankRoutingScheme: Option[String],
                       toCounterpartyBankRoutingAddress: Option[String],
                       toCounterpartyName: Option[String],
                       transactionPostedDate: Option[String],
                       toCounterpartyRoutingAddress: Option[String],
                       fromAccountBankId: Option[String],
                       transactionRequestType: Option[String],
                       toCounterpartyCurrency: Option[String],
                       `type`: Option[String],
                       username: Option[String],
                       transactionChargeAmount: Option[String],
                       transactionDescription: Option[String],
                       transactionCurrency: Option[String],
                       fromAccountId: Option[String],
                       transactionAmount: Option[String],
                       toCounterpartyId: Option[String],
                       toCounterpartyRoutingScheme: Option[String],
                       fromAccountName: Option[String],
                       transactionChargeCurrency: Option[String],
                       transactionId: Option[String]
                      )

case class FxRate(inverseConversionValue: Option[Double],
                  toCurrencyCode: Option[String],
                  conversionValue: Option[Double],
                  fromCurrencyCode: Option[String],
                  effectiveDate: Option[String])

case class Atm(name: Option[String],
               bankId: Option[String],
               meta: Option[Meta],
               location: Option[Location],
               address: Option[Address],
               id: Option[String])

case class Balance(currency: Option[String], amount: Option[String])

case class Account(owners: Seq[String],
                   generatePublicView: Boolean,
                   generateAuditorsView: Boolean,
                   number: Option[String],
                   label: Option[String],
                   IBAN: Option[String],
                   generateAccountantsView: Boolean,
                   balance: Option[Balance],
                   `type`: Option[String],
                   id: Option[String],
                   bank: Option[String],
                   branchId: Option[String],
                   accountRoutingAddress: Option[String],
                   accountRoutingScheme: Option[String])

case class Customer(name: Option[String],
                    number: Option[String])

case class CrmEvent(
                     category: Option[String],
                     actualDate: Option[String],
                     detail: Option[String],
                     bankId: Option[String],
                     id: Option[String],
                     channel: Option[String])


case class Bank(website: Option[String],
                logo: Option[String],
                fullName: Option[String],
                shortName: Option[String],
                id: Option[String])

case class Product(category: Option[String],
                   superFamily: Option[String],
                   code: Option[String],
                   name: Option[String],
                   family: Option[String],
                   meta: Option[Meta],
                   bankId: Option[String],
                   moreInfoUrl: Option[String])

case class CounterParty(thisViewId: Option[String],
                        otherBankRoutingScheme: Option[String],
                        thisBankId: Option[String],
                        thisAccountId: Option[String],
                        otherBranchRoutingAddress: Option[String],
                        otherAccountRoutingAddress: Option[String],
                        otherBankRoutingAddress: Option[String],
                        createdByUserId: Option[String],
                        counterpartyId: Option[String],
                        otherAccountRoutingScheme: Option[String],
                        otherBranchRoutingScheme: Option[String],
                        isBeneficiary: Boolean,
                        name: Option[String])

case class TransactionRequestType(chargeSummary: Option[String],
                                  createDate: Option[String],
                                  id: Option[String],
                                  updateDate: Option[String],
                                  bankId: Option[String],
                                  chargeCurrency: Option[String],
                                  chargeAmount: Option[String],
                                  transactionRequestType: Option[String])

case class Example(branches: Seq[Branch],
                   users: Seq[User],
                   transactions: Seq[Transaction],
                   fxRates: Seq[FxRate],
                   atms: Seq[Atm],
                   accounts: Seq[Account],
                   crmEvents: Seq[CrmEvent],
                   banks: Seq[Bank],
                   products: Seq[Product],
                   counterParties: Option[Seq[CounterParty]],
                   transactionRequestTypes: Seq[TransactionRequestType])