package com.tesobe.obp

import akka.actor.{Actor, Props}
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.StrictLogging

/**
  * Main manager for all actors. Currently creates SouthKafkaStreamsActor only.
  *
  * Created by slavisa on 6/4/17.
  */
class ActorOrchestration(implicit val materializer: ActorMaterializer) extends Actor with StrictLogging {

  val southKafkaStreamsActor = context.system.actorOf(Props(new SouthKafkaStreamsActor()), SouthKafkaStreamsActor.name)

  /**
    * Forwards 'init' message given from Main class.
    *
    */
  override def receive: Receive = {
    case init => southKafkaStreamsActor forward init
  }
}

object ActorOrchestration {

  final val name = "ActorOrchestration"

}

