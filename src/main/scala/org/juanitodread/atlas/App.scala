package org.juanitodread.atlas

import org.juanitodread.atlas.conf.AppConf
import com.typesafe.scalalogging.LazyLogging

object App extends AppConf with LazyLogging {
  def main(args: Array[String]): Unit = {
    logger.debug("Init")
    logger.info("Inint")
    logger.info(simpleConf)
  }
}