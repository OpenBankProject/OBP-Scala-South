package com.tesobe.obp

import com.tesobe.obp.SouthKafkaStreamsActor.{Topic, TopicBusiness}

/**
  * Created by slavisa on 6/6/17.
  */
object ProcessorFactory extends Config {

  def getProcessor = {
    processorName match {
      case "localFile" => TopicBusiness(topic, LocalProcessor(RequestExtractor_Nov2016)(Main.executionContext, Main.materializer).processor)
      case "mockedSopra" => TopicBusiness(topic, LocalProcessor(RequestExtractor_Nov2016)(Main.executionContext, Main.materializer).processor)
      case "sopra" => TopicBusiness(topic, LocalProcessor(RequestExtractor_Nov2016)(Main.executionContext, Main.materializer).processor)
      case _ => TopicBusiness(topic, LocalProcessor(RequestExtractor_Nov2016)(Main.executionContext, Main.materializer).processor)
    }
  }
}
