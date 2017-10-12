package com.tesobe.obp

import com.tesobe.obp.Main.{executionContext, materializer}
import com.tesobe.obp.SouthKafkaStreamsActor.BusinessTopic
import com.tesobe.obp.june2017._

/**
  * Defines kafka topics which will be used and functions that will be applied on received message
  *
  * Open Bank Project - Scala South Adapter
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
trait ProcessorFactory {
  this: Config =>

  /**
    *
    * @return sequence of functions which will be applied in processing of North Side messages
    */
  def getProcessor = {
    processorName match {
      case "localFile" => Seq(
        BusinessTopic(topic, LocalProcessor()(executionContext, materializer).generic),
        BusinessTopic(createTopicByClassName(GetAdapterInfo.getClass.getSimpleName), LocalProcessor()(executionContext, materializer).adapterFn),
        BusinessTopic(createTopicByClassName(GetBanks.getClass.getSimpleName), LocalProcessor()(executionContext, materializer).banksFn),
        BusinessTopic(createTopicByClassName(GetBank.getClass.getSimpleName), LocalProcessor()(executionContext, materializer).bankFn),
        BusinessTopic(createTopicByClassName(GetUserByUsernamePassword.getClass.getSimpleName), LocalProcessor()(executionContext, materializer).userFn),
        BusinessTopic(createTopicByClassName(UpdateUserAccountViews.getClass.getSimpleName), LocalProcessor()(executionContext, materializer).accountsFn),
        BusinessTopic(createTopicByClassName(OutboundGetAccounts.getClass.getSimpleName), LocalProcessor()(executionContext, materializer).bankAccountsFn)

      )
      case "mockedSopra" => BusinessTopic(topic, LocalProcessor()(executionContext, materializer).generic)
      case "sopra" => BusinessTopic(topic, LocalProcessor()(executionContext, materializer).generic)
      case _ => BusinessTopic(topic, LocalProcessor()(executionContext, materializer).generic)
    }
  }
}
