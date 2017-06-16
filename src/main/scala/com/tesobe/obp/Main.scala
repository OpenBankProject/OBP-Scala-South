package com.tesobe.obp

import akka.actor.{ActorSystem, Props}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Supervision}
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Initialize actor system and as final step sends message to ActorOrchestration to initialize all actors that will be used.
  *
  */
object Main extends App with StrictLogging with Config with ProcessorFactory {

  val systemName = config.getString("system-name")

  /**
    * Reaction on unexpected events
    */
  val decider: Supervision.Decider = {
    case e: Throwable =>
      logger.error("Exception occurred, stopping..." + e)
      Supervision.Restart
    case _ =>
      logger.error("Unknown problem, stopping...")
      Supervision.Restart
  }

  implicit val system = ActorSystem(s"$systemName")
  implicit val materializer = ActorMaterializer(ActorMaterializerSettings(system).withSupervisionStrategy(decider))
  implicit val executionContext = system.dispatcher

  val actorOrchestration = system.actorOf(Props(new ActorOrchestration()), ActorOrchestration.name)

  actorOrchestration ! getProcessor

  //  import fs2.Stream

  Await.ready(system.whenTerminated, Duration.Inf)
}
