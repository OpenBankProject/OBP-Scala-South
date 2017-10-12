package test.scala.com.tesobe.obp

import com.typesafe.scalalogging.StrictLogging
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FunSuite, Matchers}

/**
  * Created by zhanghongwei on 09/10/2017.
  */
trait ServerSetup extends FunSuite 
  with Matchers 
  with BeforeAndAfterAll 
  with BeforeAndAfterEach
  with StrictLogging{
    
}
