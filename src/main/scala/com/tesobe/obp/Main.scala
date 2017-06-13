package com.tesobe.obp

import akka.actor.{ActorSystem, Props}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings, Supervision}
import com.tesobe.obp.SouthKafkaStreamsActor.TopicBusiness
import com.tesobe.obp.jun2017.GetBanks
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Initialize actor system and as final step sends message to ActorOrchestration to initialize all actors that will be used.
  *
  * Created by slavisa on 12/27/16.
  */
object Main extends App with StrictLogging with Config {

  /**
    *
    * @return sequence of functions which will be applied in processing of North Side messages
    */
  def getProcessor = {
    processorName match {
      case "localFile" => Seq(
        TopicBusiness(topic, LocalProcessor()(executionContext, materializer).generic),
        TopicBusiness(caseClassToTopic(GetBanks), LocalProcessor()(executionContext, materializer).banks)
      )
      case "mockedSopra" => TopicBusiness(topic, LocalProcessor()(executionContext, materializer).generic)
      case "sopra" => TopicBusiness(topic, LocalProcessor()(executionContext, materializer).generic)
      case _ => TopicBusiness(topic, LocalProcessor()(executionContext, materializer).generic)
    }
  }

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
