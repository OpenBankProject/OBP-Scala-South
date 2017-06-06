package com.tesobe.obp

import com.tesobe.obp.Main.{executionContext, materializer, processorName}
import com.tesobe.obp.SouthKafkaStreamsActor.Topic

/**
  * Created by slavisa on 6/6/17.
  */
object ProcessorFactory extends KafkaConfig {

  def getProcessor = {
    processorName match {
      case "circe_Nov2016" => (Topic("Request", "Response"), CirceProcessor(RequestExtractor_Nov2016)(Main.executionContext, Main.materializer).processor)
      case "json_Nov2016" => (Topic("Request", "Response"), Processor_Nov2016(RequestExtractor_Nov2016)(Main.executionContext).processor)
      case _ => (Topic("Request", "Response"), CirceProcessor(RequestExtractor_Nov2016)(Main.executionContext, Main.materializer).processor)
    }
  }
}
