package com.tesobe.obp
import com.tesobe.obp.Main.{caseClassToTopic, executionContext, materializer, processorName, topic}
import com.tesobe.obp.SouthKafkaStreamsActor.TopicBusiness
import com.tesobe.obp.jun2017.{GetBank, GetBanks}

/**
  * Created by slavisa on 6/14/17.
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
        TopicBusiness(topic, LocalProcessor()(executionContext, materializer).generic),
        TopicBusiness(caseClassToTopic(GetBanks.getClass.getSimpleName), LocalProcessor()(executionContext, materializer).banks),
        TopicBusiness(caseClassToTopic(GetBank.getClass.getSimpleName), LocalProcessor()(executionContext, materializer).bank)
      )
      case "mockedSopra" => TopicBusiness(topic, LocalProcessor()(executionContext, materializer).generic)
      case "sopra" => TopicBusiness(topic, LocalProcessor()(executionContext, materializer).generic)
      case _ => TopicBusiness(topic, LocalProcessor()(executionContext, materializer).generic)
    }
  }
}
