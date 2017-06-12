package com.tesobe.obp.jun2017

/**
  * Created by slavisa on 6/5/17.
  */
case class AuthInfo(userId: String, username: String)
case class GetBanks(authInfo: AuthInfo, version: String)

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
case class Banks(authInfo: AuthInfo, data: List[InboundBank])