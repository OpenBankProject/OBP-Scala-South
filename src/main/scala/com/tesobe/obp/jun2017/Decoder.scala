package com.tesobe.obp.jun2017

import com.tesobe.obp.Util
import io.circe.generic.auto._
import io.circe.parser.decode


/**
  * Responsible for processing requests based on local example_import_jun2017.json file.
  *
  */
trait Decoder extends MappedDecoder {

  def getBanks(request: GetBanks) = {
    println("Enter getBanks")
    decodeLocalFile match {
      case Left(_) => Banks(request.authInfo, List.empty[InboundBank])
      case Right(x)  => Banks(request.authInfo, List(InboundBank("Bank","Bank","Bank","Bank","Bank")))
      //case Right(x) => Banks(request.authInfo, x.banks.map(mapBankN))
    }
  }

  def getBank(request: GetBank) = {
    decodeLocalFile match {
      case Left(_) => BankWrapper(request.authInfo, None)
      case Right(x) =>
        val bankId = if (request.bankId == "1") Some("obp-bank-x-gh") else if (request.bankId == "2") Some("obp-bank-y-gh") else None
        x.banks.filter(_.id == bankId).headOption match {
          case Some(x) => BankWrapper(request.authInfo, Some(mapBankN(x)))
          case None => BankWrapper(request.authInfo, None)
        }

    }
  }
  
  def getAccounts(request: GetAccounts) = {
      println("Enter getAccounts")
       AccountsWrapper(request.authInfo,List(InboundAccountJune2017("hitest","hitest","hitest","hitest","hitest","hitest",
      "hitest","hitest",List("hitest"),List("hitest"),"hitest","hitest","hitest","hitest","hitest","hitest")))
  }

  def getAdapter(request: GetAdapterInfo) = {
    AdapterInfo(data = Some(InboundAdapterInfo("", "OBP-Scala-South", "June2017", Util.gitCommit, System.currentTimeMillis().toString)))
  }


  /*
   * Decodes example_import_jun2017.json file
   */
  private val decodeLocalFile = {
    val resource = scala.io.Source.fromResource("example_import_jun2017.json")
    val lines = resource.getLines()
    val json = lines.mkString
    decode[com.tesobe.obp.jun2017.Example](json)
  }

}


object Decoder extends Decoder
