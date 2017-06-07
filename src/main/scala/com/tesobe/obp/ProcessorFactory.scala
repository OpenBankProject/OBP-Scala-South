package com.tesobe.obp

import com.tesobe.obp.SouthKafkaStreamsActor.{Topic, TopicBusiness}

/**
  * Created by slavisa on 6/6/17.
  */
object ProcessorFactory extends KafkaConfig {

  def getProcessor = {
    processorName match {
      case "circe_Nov2016" => TopicBusiness(Topic("Request", "Response"), CirceProcessor(RequestExtractor_Nov2016)(Main.executionContext, Main.materializer).processor)
      case "json_Nov2016" => TopicBusiness(Topic("Request", "Response"), Processor_Nov2016(RequestExtractor_Nov2016)(Main.executionContext).processor)
      case _ => TopicBusiness(Topic("Request", "Response"), CirceProcessor(RequestExtractor_Nov2016)(Main.executionContext, Main.materializer).processor)
    }
  }
}
