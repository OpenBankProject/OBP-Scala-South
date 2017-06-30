package com.tesobe.obp.jun2017


/**
  * Here are defined all the things that go through kafka
  *
  */

/**
  * Carries data related to auth
  *
  * @param userId
  * @param username
  */
case class AuthInfo(userId: String, username: String)

/**
  * Payloads for request topic
  *
  */
case class GetBanks(authInfo: AuthInfo, criteria: String)
case class GetBank(authInfo: AuthInfo, bankId: String)
case class GetAdapterInfo(currentTimeInMillis: String)
case class GetAccounts(authInfo: AuthInfo, bankId: String, accountId: String)

/**
  * Payloads for response topic
  *
  */
case class Banks(authInfo: AuthInfo, data: List[InboundBank])
case class BankWrapper(authInfo: AuthInfo, data: Option[InboundBank])
case class AdapterInfo(data: Option[InboundAdapterInfo])
case class AccountsWrapper(authinfo: AuthInfo, data: List[InboundAccountJune2017])

/**
  * All subsequent case classes must be the same structure as it is defined on North Side
  *
  */
case class InboundBank(
                        errorCode: String,
                        bankId: String,
                        name: String,
                        logo: String,
                        url: String
                      )
case class UserN(
                  errorCode: Option[String],
                  email: Option[String],
                  displayName: Option[String]
                )

case class InboundAccount(
                          errorCode: String,
                          accountId: String,
                          bankId: String,
                          label: String,
                          number: String,
                          `type`: String,
                          balanceAmount: String,
                          balanceCurrency: String,
                          iban: String,
                          owners: String,
                          generatePublicView: String,
                          generateAccountantsView: String,
                          generateAuditorsView: String
                          )

abstract class InboundMessageBase(optionalFields: String*) {
                                  def errorCode: String
                                  }
case class InboundAccountJune2017(
                                   errorCode: String,
                                   bankId: String,
                                   branchId: String,
                                   accountId: String,
                                   number: String,
                                   accountType: String,
                                   balanceAmount: String,
                                   balanceCurrency: String,
                                   owners: List[String],
                                   generateViews: List[String],
                                   bankRoutingScheme:String,
                                   bankRoutingAddress:String,
                                   branchRoutingScheme:String,
                                   branchRoutingAddress:String,
                                   accountRoutingScheme:String,
                                   accountRoutingAddress:String
                                 ) extends InboundMessageBase


case class InboundAdapterInfo(errorCode: String,
                              name: String,
                              version: String,
                              git_commit: String,
                              currentTimeInMillis: String
                             )