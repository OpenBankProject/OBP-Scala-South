package com.tesobe.obp

import akka.actor.{Actor, Props}
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.StrictLogging

/**
  * Created by slavisa on 6/4/17.
  */
class ActorOrchestration(implicit val materializer: ActorMaterializer) extends Actor with StrictLogging {

  val southKafkaStreamsActor = context.system.actorOf(Props(new SouthKafkaStreamsActor()), SouthKafkaStreamsActor.name)

  override def receive: Receive = {
    case init => southKafkaStreamsActor forward init
  }
}

object ActorOrchestration {

  final val name = "ActorOrchestration"

}

