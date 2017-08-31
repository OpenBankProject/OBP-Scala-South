package com.tesobe

import org.mockserver.integration.ClientAndServer.startClientAndServer
import org.mockserver.model.Header
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response


object RunMockServer{
  val mockServer = startClientAndServer(1080)
  
  def jsonToString(filename: String): String = {
    val source = scala.io.Source.fromResource(filename)
    val lines = try source.mkString finally source.close()
    lines
  }
  
  def startMockServer = {
    // 1
    mockServer
      .when(
        request()
          .withMethod("POST")
          //.withHeader("Content-Type","application/json;charset=utf-8")
          .withPath("/1")
          //.withBody("body")
      )
      .respond(
        response()
          .withStatusCode(401)
          .withHeaders(
            new Header("Content-Type", "application/json; charset=utf-8")
          )
          .withBody(jsonToString("example_import_Nov2016.json"))
      )
    //2 
    mockServer
      .when(
        request()
          .withMethod("POST")
          //.withHeader("Content-Type","application/json;charset=utf-8")
          .withPath("/1")
        //.withBody("body")
      )
      .respond(
        response()
          .withStatusCode(401)
          .withHeaders(
            new Header("Content-Type", "application/json; charset=utf-8")
          )
          .withBody(jsonToString("example_import_Nov2016.json"))
      )
  }
}
