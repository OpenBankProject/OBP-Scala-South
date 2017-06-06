package com.tesobe.obp

/**
  * Created by slavisa on 6/6/17.
  */
object RequestExtractor_Nov2016 extends RequestExtractor{

  override def extractQuery(request: Request): (String, String) = {
    if (request.version == "Nov2016") {
      (request.target.get, request.name.get)
    } else {
      ("" , "")
    }
  }

}
