package com.tesobe.obp.jun2017

import io.circe.generic.auto._
import io.circe.parser.decode


/**
  * Responsible for processing requests based on local example_import_jun2017.json file.
  *
  * Created by slavisa on 6/8/17.
  */
trait Decoder extends MappedDecoder {

  def getBanks(request: GetBanks) = {
    decodeLocalFile match {
      case Left(_) => Banks(request.authInfo, List.empty[InboundBank])
      case Right(x) => Banks(request.authInfo, x.banks.map(mapBankN))
    }
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
