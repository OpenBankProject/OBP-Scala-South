package com.tesobe.obp

import akka.actor.{Actor, Props}
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.StrictLogging

/**
  * Main manager for all actors. Currently creates SouthKafkaStreamsActor only.
  *
  * Open Bank Project - Leumi Adapter
  * Copyright (C) 2016-2017, TESOBE Ltd.This program is free software: you can redistribute it and/or modify
  * it under the terms of the GNU Affero General Public License as published by
  * the Free Software Foundation, either version 3 of the License, or
  * (at your option) any later version.This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  * GNU Affero General Public License for more details.You should have received a copy of the GNU Affero General Public License
  * along with this program.  If not, see <http://www.gnu.org/licenses/>.Email: contact@tesobe.com
  * TESOBE Ltd
  * Osloerstrasse 16/17
  * Berlin 13359, GermanyThis product includes software developed at TESOBE (http://www.tesobe.com/)
  * This software may also be distributed under a commercial license from TESOBE Ltd subject to separate terms.
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

