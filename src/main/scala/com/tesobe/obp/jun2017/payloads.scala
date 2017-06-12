package com.tesobe.obp.jun2017

/**
  * Here are defined all the things that go through kafka
  *
  * Created by slavisa on 6/5/17.
  */

/**
  * Carries data related to auth
  *
  * @param userId
  * @param username
  */
case class AuthInfo(userId: String, username: String)

/**
  * Payload for GetBank request topic
  *
  * @param authInfo
  * @param version
  */
case class GetBanks(authInfo: AuthInfo, version: String)

/**
  * Payload for GetBank response topic
  *
  * @param authInfo
  * @param data
  */
case class Banks(authInfo: AuthInfo, data: List[InboundBank])


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

