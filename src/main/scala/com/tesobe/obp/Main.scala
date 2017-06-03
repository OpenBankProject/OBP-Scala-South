package com.tesobe.obp

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

/**
  * Created by slavisa on 12/27/16.
  */
object Main extends App with KafkaConfig {

  val config = ConfigFactory.load()
  val systemName = config.getString("system-name")

  implicit val system = ActorSystem(s"$systemName-module")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  println("........................")

}
