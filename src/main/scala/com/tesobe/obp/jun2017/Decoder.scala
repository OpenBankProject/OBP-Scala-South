package com.tesobe.obp.jun2017

import io.circe.generic.auto._
import io.circe.parser.decode


/**
  * Created by slavisa on 6/8/17.
  */
trait Decoder extends MappedDecoder {

  def getBanks(d: GetBanks) = {
    val resource = scala.io.Source.fromResource("example_import_jun2017.json")
    val lines = resource.getLines()
    val json = lines.mkString
    decode[com.tesobe.obp.jun2017.Example](json) match {
      case Left(_) => Banks(d.authInfo, List.empty[InboundBank])
      case Right(x) => Banks(d.authInfo, x.banks.map(mapBankN))
    }
  }

}


object Decoder extends Decoder
