package com.tesobe.obp

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

/**
  * Created by slavisa on 6/5/17.
  */
trait JSonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val lobbyFormat = jsonFormat1(Lobby)
  implicit val driveUpFormat = jsonFormat1(DriveUp)
  implicit val locationFormat = jsonFormat2(Location)
  implicit val roleFormat = jsonFormat2(Role)
  implicit val userFormat = jsonFormat4(User)
  implicit val balanceFormat = jsonFormat2(Balance)
  implicit val licenceFormat = jsonFormat2(Licence)
  implicit val metaFormat = jsonFormat1(Meta)

  implicit val accountFormat = jsonFormat14(Account)
  implicit val addressFormat = jsonFormat8(Address)

  implicit val atmFormat = jsonFormat6(Atm)
  implicit val bankNFormat = jsonFormat4(BankN)
  implicit val bankFormat = jsonFormat5(Bank)
  implicit val branchFormat = jsonFormat8(Branch)
  implicit val counterPartyFormat = jsonFormat13(CounterParty)
  implicit val counterPartySimpleFormat = jsonFormat2(CounterPartySimple)
  implicit val crmEventFormat = jsonFormat6(CrmEvent)
  implicit val customerFormat = jsonFormat2(Customer)
  implicit val detailsFormat = jsonFormat6(Details)
  implicit val fxRateFormat = jsonFormat5(FxRate)
  implicit val productFormat = jsonFormat8(Product)
  implicit val thisAccountFormat = jsonFormat2(ThisAccount)
  implicit val transactionFormat = jsonFormat22(Transaction)
  implicit val transactionRequestTypeFormat = jsonFormat8(TransactionRequestType)
  implicit val exampleFormat = jsonFormat11(Example)


  implicit val requestFormat = jsonFormat9(Request)
  implicit val responseFormat = jsonFormat5(Response)


}
