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

/**
  * Payloads for response topic
  *
  */
case class Banks(authInfo: AuthInfo, data: List[InboundBank])
case class BankWrapper(authInfo: AuthInfo, data: Option[InboundBank])
case class AdapterInfo(data: Option[InboundAdapterInfo])

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
case class InboundAdapterInfo(errorCode: String,
                              name: String,
                              version: String,
                              git_commit: String,
                              currentTimeInMillis: String
                             )