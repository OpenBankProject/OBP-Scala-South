package com.tesobe.obp.june2017

import java.util.Date


/**
  * Here are defined all the things that go through kafka
  *
  * Open Bank Project - Scala South Adapter
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
  *
  */

/**
  * case classes used to define topics
  */
sealed trait TopicCaseClass
/**
  * Payloads for request topic
  *
  */
case class GetBanks(authInfo: AuthInfo, criteria: String) extends TopicCaseClass
case class GetBank(authInfo: AuthInfo, bankId: String) extends TopicCaseClass
case class GetAdapterInfo(date: String) extends TopicCaseClass
case class OutboundGetAccounts(authInfo: AuthInfo, customers:InternalCustomers)  extends TopicCaseClass
case class GetAccountbyAccountID(authInfo: AuthInfo, bankId: String, accountId: String) extends TopicCaseClass
case class GetAccountbyAccountNumber(authInfo: AuthInfo, bankId: String, accountNumber: String) extends TopicCaseClass
case class GetUserByUsernamePassword(authInfo: AuthInfo, password: String) extends TopicCaseClass
case class UpdateUserAccountViews(authInfo: AuthInfo, password: String) extends TopicCaseClass
case class GetTransactiondfss(authInfo: AuthInfo,bankId: String, accountId: String, limit: Int, fromDate: String, toDate: String) extends TopicCaseClass
case class GetTransactions(authInfo: AuthInfo, bankId: String, accountId: String, limit: Int, fromDate: String, toDate: String) extends TopicCaseClass
case class GetTransaction(authInfo: AuthInfo, bankId: String, accountId: String, transactionId: String) extends TopicCaseClass
case class GetToken(username: String) extends TopicCaseClass
case class CreateTransaction(
  authInfo: AuthInfo,
  
  // fromAccount
  fromAccountBankId : String,
  fromAccountId : String,
  
  // transaction details
  transactionRequestType: String,
  transactionChargePolicy: String,
  transactionRequestCommonBody: TransactionRequestCommonBodyJSON,
  
  // toAccount or toCounterparty
  toCounterpartyId: String,
  toCounterpartyName: String,
  toCounterpartyCurrency: String,
  toCounterpartyRoutingAddress: String,
  toCounterpartyRoutingScheme: String,
  toCounterpartyBankRoutingAddress: String,
  toCounterpartyBankRoutingScheme: String

) extends TopicCaseClass

case class OutboundCreateChallengeJune2017(
  authInfo: AuthInfo,
  bankId: String,
  accountId: String,
  userId: String,
  username: String,
  transactionRequestType: String,
  transactionRequestId: String,
  phoneNumber: String
) extends TopicCaseClass

/**
  * Payloads for response topic
  *
  */
case class Banks(authInfo: AuthInfo, data: List[InboundBank])
case class BankWrapper(authInfo: AuthInfo, data: Option[InboundBank])
case class AdapterInfo(data: Option[InboundAdapterInfo])
case class UserWrapper(data: Option[InboundValidatedUser])
case class OutboundUserAccountViewsBaseWapper(data: List[InboundAccountJune2017])
case class InboundBankAccounts(authInfo: AuthInfo, data: List[InboundAccountJune2017])
case class InboundBankAccount(authInfo: AuthInfo, data: InboundAccountJune2017)
case class InboundTransactions(authInfo: AuthInfo, data: List[InternalTransaction])
case class InboundTransaction(authInfo: AuthInfo, data: InternalTransaction)
case class InboundToken(username: String, token: String)
case class InboundCreateTransactionId(authInfo: AuthInfo, data: InternalTransactionId)
case class InboundCreateChallengeJune2017(authInfo: AuthInfo, data: InternalCreateChallengeJune2017)

/**
  * All subsequent case classes must be the same structure as it is defined on North Side
  *
  */
case class AuthInfo(userId: String, username: String, cbsToken: String)

case class InboundBank(
  errorCode: String,
  bankId: String,
  name: String,
  logo: String,
  url: String
)
case class InboundValidatedUser(
  errorCode: Option[String],
  email: Option[String],
  displayName: Option[String]
)

case class InboundAdapterInfo(
  errorCode: String,
  name: String,
  version: String,
  git_commit: String,
  date: String
)

case class InboundAccountJune2017(
  errorCode: String,
  cbsToken: String,
  bankId: String,
  branchId: String,
  accountId: String,
  accountNumber: String,
  accountType: String,
  balanceAmount: String,
  balanceCurrency: String,
  owners: List[String],
  viewsToGenerate: List[String],
  bankRoutingScheme: String,
  bankRoutingAddress: String,
  branchRoutingScheme: String,
  branchRoutingAddress: String,
  accountRoutingScheme: String,
  accountRoutingAddress: String
)

abstract class InboundMessageBase(optionalFields: String*) {
  def errorCode: String
}

case class InternalTransaction(
  errorCode: String,
  transactionId: String, 
  accountId: String, 
  amount: String, 
  bankId: String, 
  completedDate: String, 
  counterpartyId: String,
  counterpartyName: String,
  currency: String, 
  description: String,
  newBalanceAmount: String, 
  newBalanceCurrency: String,
  postedDate: String, 
  `type`: String, 
  userId: String 
) extends InboundMessageBase


// these are for create transactions
case class AmountOfMoneyJsonV121(
  currency: String,
  amount: String
)

case class TransactionRequestAccountJsonV140(
  bank_id: String,
  account_id: String
)

//For SEPA, it need the iban to find the toCounterpaty--> toBankAccount
case class IbanJson(val iban: String)

//For COUNTERPATY, it need the counterparty_id to find the toCounterpaty--> toBankAccount
case class CounterpartyIdJson(val counterparty_id: String)

//high level of four different kinds of transaction request types: FREE_FROM, SANDBOXTAN, COUNTERPATY and SEPA.
//They share the same AmountOfMoney and description fields
//Note : in scala case-to-case inheritance is prohibited, so used trait instead
sealed trait TransactionRequestCommonBodyJSON {
  val value: AmountOfMoneyJsonV121
  val description: String
}
// the common parts of four types
// note: there is TransactionRequestCommonBodyJSON trait, so this case class call TransactionRequestBodyCommonJSON
case class TransactionRequestBodyCommonJSON(
  value: AmountOfMoneyJsonV121,
  description: String
) extends TransactionRequestCommonBodyJSON

// the data from endpoint, extract as valid JSON
case class TransactionRequestBodySandBoxTanJSON(
  to: TransactionRequestAccountJsonV140,
  value: AmountOfMoneyJsonV121,
  description: String
) extends TransactionRequestCommonBodyJSON

case class TransactionRequestBodyCounterpartyJSON(
  to: CounterpartyIdJson,
  value: AmountOfMoneyJsonV121,
  description: String,
  charge_policy: String
) extends TransactionRequestCommonBodyJSON

// the data from endpoint, extract as valid JSON
case class TransactionRequestBodySEPAJSON(
  value: AmountOfMoneyJsonV121,
  to: IbanJson,
  description: String,
  charge_policy: String
) extends TransactionRequestCommonBodyJSON

case class CounterpartyTransferToPhoneJson(
  other_account_message: String,
  other_account_phone_number: String
)

case class TransactionRequestBodyTransferToPhoneJson(
  value: AmountOfMoneyJsonV121,
  description: String,
  from_account_phone_number: String,
  from_account_owner_nickname: String,
  couterparty: CounterpartyTransferToPhoneJson
) extends TransactionRequestCommonBodyJSON

case class CounterpartyTransferToAtmJson(
  other_account_owner: String,
  other_account_owner_passport_id_or_national_id: String,
  other_account_owner_id_type: String,
  other_account_owner_birthday: String,
  other_account_message: String,
  other_account_phone_number: String
)

case class TransactionRequestBodyTransferToAtmJson(
  value: AmountOfMoneyJsonV121,
  description: String,
  from_account_phone_number: String,
  from_account_owner_nickname: String,
  charge_policy: String,
  couterparty: CounterpartyTransferToAtmJson
) extends TransactionRequestCommonBodyJSON

case class CounterpartyTransferToAccount(
  other_account_owner: String,
  transfer_type: String,
  transfer_is_scheduled : String,
  future_date : String,
  bank_code:String,
  branch_number: String,
  account_number: String,
  iban: String
)

case class TransactionRequestBodyTransferToAccount(
  value: AmountOfMoneyJsonV121,
  description: String,
  charge_policy: String,
  couterparty: CounterpartyTransferToAccount
) extends TransactionRequestCommonBodyJSON

case class InternalTransactionId(
  id : String
)

case class InternalCreateChallengeJune2017(
  answer : String
)

case class CustomerFaceImageJson(
  url: String,
  date: String
)

case class CustomerCreditRatingJSON(
  rating: String,
  source: String
)

case class InternalCustomer(
  bankId:String,
  customerId: String,
  customerNumber: String,
  legalName: String,
  dateOfBirth: String
)

case class InternalCustomers(customers: List[InternalCustomer])