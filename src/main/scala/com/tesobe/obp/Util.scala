package com.tesobe.obp

import com.typesafe.scalalogging.StrictLogging


/**
  * Created by marko on 6/29/17.
  */
object Util extends StrictLogging {
  /*
    Return the git commit. If we can't for some reason (not a git root etc) then log and return ""
     */
  def gitCommit : String = {
    val commit = try {
      val properties = new java.util.Properties()
      logger.debug("Before getResourceAsStream git.properties")
      properties.load(getClass().getClassLoader().getResourceAsStream("git.properties"))
      logger.debug("Before get Property git.commit.id")
      properties.getProperty("git.commit.id", "")
    } catch {
      case e : Throwable => {
        logger.warn("gitCommit says: Could not return git commit. Does resources/git.properties exist?")
        logger.error(s"Exception in gitCommit: $e")
        "" // Return empty string
      }
    }
    commit
  }
}
